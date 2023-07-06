package wily.factocrafty.util;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.MultiBufferSource;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class ScreenUtil {

        public static float getRed(int color) {
            return (color >> 16 & 0xFF) / 255.0F;
        }

        public static float getGreen(int color) {
            return (color >> 8 & 0xFF) / 255.0F;
        }

        public static float getBlue(int color) {
            return (color & 0xFF) / 255.0F;
        }

        public static float getAlpha(int color) {
            return (color >> 24 & 0xFF) / 255.0F;
        }

        public static Minecraft minecraft() {
            return Minecraft.getInstance();
        }



        public static void drawString(PoseStack stack, String text, int x, int y, int color, boolean shadow) {
            Font font = minecraft().font;
            MultiBufferSource.BufferSource source = minecraft().renderBuffers().bufferSource();
            font.drawInBatch(text, (float)x, (float)y, color, shadow, stack.last().pose(), source, Font.DisplayMode.NORMAL, 0, 15728880, font.isBidirectional());
            RenderSystem.disableDepthTest();
            source.endBatch();
            RenderSystem.enableDepthTest();
        }

        public static void prepTextScale(PoseStack poseStack, Consumer<PoseStack> runnable, float x, float y, float scale) {
            float yAdd = 4 - (scale * 8) / 2F;
            poseStack.pushPose();
            poseStack.translate(x, y + yAdd, 0);
            poseStack.scale(scale, scale, scale);
            runnable.accept(poseStack);
            poseStack.popPose();
            RenderSystem.setShaderColor(1, 1, 1, 1);
        }

    public static void drawGUIBackground(GuiGraphics graphics, int x, int y, int width, int height, int outlineColor, int mainColor, int shadowColor, int lightColor){
        graphics.fill(x + 2, y + 2, x + width - 2, y + height - 2, mainColor);


        graphics.fill(x + 2, y + 1, x + width - 3, y + 3, lightColor);
        graphics.fill(x + 3, y + 3, x + 4, y + 4, lightColor);
        graphics.fill(x + 1, y + 2, x + 3, y + height - 3, lightColor);
        graphics.fill(x + 3, y + height - 1, x + width - 3, y + height - 3, shadowColor);
        graphics.fill(x + width - 1, y + 3, x + width - 3, y + height - 3, shadowColor);
        graphics.fill(x + width - 4, y + height - 4, x + width - 2, y + height - 2, shadowColor);

        graphics.fill(x + 2, y, x + width - 3, y + 1, outlineColor);
        graphics.fill(x + 3, y + height - 1, x + width - 3, y + height, outlineColor);
        graphics.fill(x, y + 2, x + 1, y + height - 3, outlineColor);
        graphics.fill(x + width - 1, y + 3, x + width, y + height - 3, outlineColor);
        BiConsumer<Integer, Integer> outlinePixel = (posx, posy)-> graphics.fill(posx, posy, posx + 1, posy + 1, outlineColor);
        outlinePixel.accept(x + 1, y + 1);
        outlinePixel.accept(x + width - 2, y + 1);
        outlinePixel.accept(x + width - 1, y + 2);
        outlinePixel.accept(x + 1, y + height - 3);
        outlinePixel.accept(x + 2, y + height - 2);
        outlinePixel.accept(x+ width -2, y + height - 3);
        outlinePixel.accept(x+ width -3, y + height - 2);

    }
    public static void drawGUIBackground(GuiGraphics graphics, int x, int y, int width, int height) {
        drawGUIBackground(graphics, x,y,width,height,-16777216, -3750202, -11184811, -1);
    }
    public static void drawGUISlot(GuiGraphics graphics, int x, int y, int width, int height, int shadowColor, int lightColor, int cornerColor, Integer backGroundColor) {
        graphics.fill(x, y, x + width - 1, y + 1, shadowColor);
        graphics.fill(x, y + 1, x + 1, y + height - 1, shadowColor);
        graphics.fill(x + 1, y + height - 1, x + width, y + height, lightColor);
        graphics.fill(x + width - 1, y + 1, x + width, y + height - 1, lightColor);
        graphics.fill(x + width - 1, y + 1, x + width, y + height - 1, lightColor);
        graphics.fill(x + width - 1, y, x + width, y + 1, cornerColor);
        graphics.fill(x , y + height - 1, x + 1, y + height, cornerColor);
        if (backGroundColor != null)
            graphics.fill(x + 1, y + 1, x + width - 1, y + height - 1, backGroundColor);
    }
    public static void drawGUISlot(GuiGraphics graphics, int x, int y, int width, int height) {
        drawGUISlot(graphics,x,y,width,height, -13158601, -1, -7631989, -7631989);
    }
    public static void drawGUISubSlot(GuiGraphics graphics, int x, int y, int width, int height) {
        drawGUISlot(graphics,x,y,width,height, -16777216, -1, -13158601, -14342875);
    }
    public static void drawGUIFluidSlot(GuiGraphics graphics, int x, int y, int width, int height) {
        drawGUISlot(graphics,x,y,width,height, -13158601, -1, -7631989, -8947849);
    }
    public static void drawGUISlotOutline(GuiGraphics graphics, int x, int y, int width, int height) {
        drawGUISlot(graphics,x,y,width,height, -10263709, -5066062, -8158333, null);
    }

        public static void renderScaled(PoseStack stack, String text, int x, int y, float scale, int color, boolean shadow) {
            prepTextScale(stack, m -> drawString(stack, text, 0, 0, color, shadow), x, y, scale);
        }

}
