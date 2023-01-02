package wily.factocrafty.client.screens;

import net.minecraft.client.renderer.Rect2i;

public interface IWidget {
    Rect2i getBounds();

    boolean isVisible();
}
