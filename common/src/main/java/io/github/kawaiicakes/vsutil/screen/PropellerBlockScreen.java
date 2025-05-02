package io.github.kawaiicakes.vsutil.screen;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.logging.LogUtils;
import io.github.kawaiicakes.vsutil.network.UpdatePropellerPacket;
import io.github.kawaiicakes.vsutil.network.VSUtilPackets;
import io.github.kawaiicakes.vsutil.tournament.blockentity.PropellerBlockEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

import java.lang.ref.WeakReference;
import java.util.Objects;

@Environment(EnvType.CLIENT)
public class PropellerBlockScreen extends Screen {
    private final WeakReference<PropellerBlockEntity<?>> prop;
    private NumberBox force;
    private NumberBox maxSpeed;
    private NumberBox accel;
    private boolean finalize;

    public PropellerBlockScreen(PropellerBlockEntity<?> propeller, Component title) {
        super(title);
        this.prop = new WeakReference<>(propeller);
    }

    @Override
    protected void init() {
        super.init();

        assert this.minecraft != null;
        this.minecraft.keyboardHandler.setSendRepeatsToGui(true);

        PropellerBlockEntity<?> prop = this.prop.get();
        if (prop == null) {
            this.onCancel();
            return;
        }

        this.addRenderableWidget(new Button(
                this.width / 2 - 4 - 150, 210,
                150, 20,
                CommonComponents.GUI_DONE,
                button -> this.onDone())
        );
        this.addRenderableWidget(new Button(
                this.width / 2 + 4, 210,
                150, 20,
                CommonComponents.GUI_CANCEL,
                button -> this.onCancel())
        );

        this.addRenderableWidget(
                CycleButton.builder(
                        boolValue -> Component.translatable("screen.vsutil.prop_" + boolValue)
                                .withStyle((Boolean) boolValue ? ChatFormatting.RED : ChatFormatting.GREEN)
                )
                .withValues(
                        Boolean.TRUE,
                        Boolean.FALSE
                )
                .displayOnlyValue()
                .withInitialValue(Boolean.FALSE)
                .create(
                        this.width / 2 - 4 - 150, 185,
                        308, 20,
                        Component.literal("FINALIZE"),
                        (cycleButton, boolValue) -> this.finalize = (boolean) boolValue
                )
        );

        this.force = new NumberBox(this.font, this.width / 2 - 3 - 150, 64, "force");
        // digits in max double value
        this.force.setMaxLength(309);
        this.force.setValue(Double.toString(prop.getForce()));
        this.addWidget(this.force);

        this.maxSpeed = new NumberBox(this.font, this.width / 2 - 3 - 150, 110, "maxSpeed");
        // digits in max float value
        this.maxSpeed.setMaxLength(128);
        this.maxSpeed.setValue(Float.toString(prop.getMaxSpeed()));
        this.addWidget(this.maxSpeed);

        this.accel = new NumberBox(this.font, this.width / 2 - 3 - 150, 156, "accel");
        // digits in max float value
        this.accel.setMaxLength(128);
        this.accel.setValue(Float.toString(prop.getAcceleration()));
        this.addWidget(this.accel);

        this.setInitialFocus(this.force);
    }

    @Override
    public void resize(@NotNull Minecraft minecraft, int width, int height) {
        super.resize(minecraft, width, height);
        String string = this.force.getValue();
        String string2 = this.maxSpeed.getValue();
        String string3 = this.accel.getValue();
        this.init(minecraft, width, height);
        this.force.setValue(string);
        this.maxSpeed.setValue(string2);
        this.accel.setValue(string3);
    }

    @Override
    public void removed() {
        super.removed();
        assert this.minecraft != null;
        this.minecraft.keyboardHandler.setSendRepeatsToGui(false);
    }

    @Override
    public void onClose() {
        super.onClose();
        this.onCancel();
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (super.keyPressed(keyCode, scanCode, modifiers)) {
            return true;
        }
        if (keyCode == 257 || keyCode == 335) {
            this.onDone();
            return true;
        }
        return false;
    }

    @Override
    public void render(@NotNull PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
        PropellerBlockEntity<?> prop = this.prop.get();
        if (prop == null) {
            this.onCancel();
            return;
        }

        this.renderBackground(poseStack);
        drawCenteredString(
                poseStack,
                this.font,
                Component.translatable("screen.vsutil.prop_stats"),
                this.width / 2,
                35,
                0xFFFFFF
        );

        drawString(
                poseStack,
                this.font,
                Component.translatable("screen.vsutil.prop_force", prop.getMaxConfigForce()),
                this.force.x,
                52,
                0xA0A0A0
        );
        this.force.render(poseStack, mouseX, mouseY, partialTick);

        drawString(
                poseStack,
                this.font,
                Component.translatable("screen.vsutil.prop_maxSpeed", prop.getMaxConfigSpeed()),
                this.maxSpeed.x,
                98,
                0xA0A0A0
        );
        this.maxSpeed.render(poseStack, mouseX, mouseY, partialTick);

        drawString(
                poseStack,
                this.font,
                Component.translatable("screen.vsutil.prop_accel", prop.getMaxConfigAcceleration()),
                this.accel.x,
                144,
                0xA0A0A0
        );
        this.accel.render(poseStack, mouseX, mouseY, partialTick);

        super.render(poseStack, mouseX, mouseY, partialTick);
    }

    @Override
    public void tick() {
        super.tick();
        this.force.tick();
        this.maxSpeed.tick();
        this.accel.tick();
    }

    private void onDone() {
        if (this.sendToServer()) {
            assert this.minecraft != null;
            this.minecraft.setScreen(null);
        }
    }

    private void onCancel() {
        assert this.minecraft != null;
        this.minecraft.setScreen(null);
    }

    private boolean sendToServer() {
        try {
            assert this.minecraft != null;

            PropellerBlockEntity<?> prop = this.prop.get();
            if (prop == null) {
                this.onCancel();
                return true;
            }

            // These are clientside checks so players know they did something wrong without a delay from the server.
            // This is strictly aesthetic; actual anti-cheat checks occur serverside.
            if (prop.isUneditable()) {
                assert this.minecraft.player != null;
                this.minecraft.player.sendSystemMessage(
                        Component.translatable("error.vsutil.not_editable").withStyle(ChatFormatting.RED)
                );
                this.onCancel();
                return true;
            }

            // TODO - cache config max values from server, then do clientside check for user QoL purposes
            //  Also use cached values in #render above

            VSUtilPackets.sendToServer(UpdatePropellerPacket.create(
                    Objects.requireNonNull(prop.getLevel()).dimension(),
                    prop.getBlockPos(),
                    this.force.valAsDouble(),
                    this.maxSpeed.valAsFloat(),
                    this.accel.valAsFloat(),
                    this.finalize
            ));

            return true;
        } catch (RuntimeException e) {
            LogUtils.getLogger().error(Component.translatable("error.vsutil.packet_send").getString(), e);
            return false;
        }
    }

    public static class NumberBox extends EditBox {
        public NumberBox(Font font, int x, int y, String message) {
            super(font, x, y, 306, 20, Component.translatable("screen.vsutil.prop_" + message));
        }

        public double valAsDouble() {
            return Double.parseDouble(this.getValue());
        }

        public float valAsFloat() {
            return Float.parseFloat(this.getValue());
        }
    }
}
