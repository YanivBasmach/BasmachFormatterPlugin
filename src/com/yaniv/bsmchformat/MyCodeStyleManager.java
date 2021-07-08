package com.yaniv.bsmchformat;

import com.intellij.application.options.CodeStyle;
import com.intellij.formatting.Block;
import com.intellij.formatting.FormattingModel;
import com.intellij.formatting.FormattingModelBuilder;
import com.intellij.formatting.SpacingImpl;
import com.intellij.lang.LanguageFormatting;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Couple;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiFile;
import com.intellij.psi.codeStyle.CodeStyleSettings;
import com.intellij.psi.formatter.java.BlockContainingJavaBlock;
import com.intellij.psi.formatter.java.CodeBlockBlock;
import com.intellij.psi.impl.source.codeStyle.CodeStyleManagerImpl;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;

public class MyCodeStyleManager extends CodeStyleManagerImpl {
  public MyCodeStyleManager(Project project) {
    super(project);
  }

  @Override
  public int getMinLineFeeds(@NotNull PsiFile file, int offset) {
    System.out.println("getting min line feeds");
    FormattingModel model = createFormattingModel(file);
    if (model != null) {
      Couple<Block> blockWithParent = getBlockAtOffset(null, model.getRootBlock(), offset);
      if (blockWithParent != null) {
        Block parentBlock = blockWithParent.first;
        Block targetBlock = blockWithParent.second;
        if (parentBlock != null && targetBlock != null) {
          Block prevBlock = findPreviousSibling(parentBlock, targetBlock);
          if (prevBlock != null) {
            if (parentBlock instanceof CodeBlockBlock) {
              if (targetBlock instanceof BlockContainingJavaBlock) {
                System.out.println("adding 2 blank lines before " + targetBlock);
                return 2;
              }
            }
            SpacingImpl spacing = (SpacingImpl) parentBlock.getSpacing(prevBlock, targetBlock);
            return spacing == null ? -1 : spacing.getMinLineFeeds();
          }
        }
      }
    }
    return -1;
  }

  @Nullable
  private static Block findPreviousSibling(@NotNull Block parent, Block block) {
    Block result = null;

    Block subBlock;
    for(Iterator var3 = parent.getSubBlocks().iterator(); var3.hasNext(); result = subBlock) {
      subBlock = (Block)var3.next();
      if (subBlock == block) {
        return result;
      }
    }

    return null;
  }

  @Nullable
  private static FormattingModel createFormattingModel(@NotNull PsiFile file) {
    FormattingModelBuilder builder = LanguageFormatting.INSTANCE.forContext(file);
    if (builder == null) {
      return null;
    } else {
      CodeStyleSettings settings = CodeStyle.getSettings(file);
      return builder.createModel(file, settings);
    }
  }

  @Nullable
  private static Couple<Block> getBlockAtOffset(@Nullable Block parent, @NotNull Block block, int offset) {
    TextRange textRange = block.getTextRange();
    int startOffset = textRange.getStartOffset();
    int endOffset = textRange.getEndOffset();
    if (startOffset == offset) {
      return Couple.of(parent, block);
    } else if (startOffset <= offset && endOffset >= offset && !block.isLeaf()) {
      Iterator var6 = block.getSubBlocks().iterator();

      Couple<Block> result;
      do {
        if (!var6.hasNext()) {
          return null;
        }

        Block subBlock = (Block)var6.next();
        result = getBlockAtOffset(block, subBlock, offset);
      } while(result == null);

      return result;
    } else {
      return null;
    }
  }
}
