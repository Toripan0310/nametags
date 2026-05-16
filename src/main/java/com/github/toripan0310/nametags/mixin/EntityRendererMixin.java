package com.github.toripan0310.nametags.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = EntityRenderer.class)
public abstract class EntityRendererMixin<T extends Entity> {

	@Shadow @Final
	protected net.minecraft.client.renderer.entity.EntityRenderDispatcher entityRenderDispatcher;

	@Shadow
	public abstract Font getFont();

	@Inject(method = "shouldShowName", at = @At("HEAD"), cancellable = true)
	private void alwaysShowName(T entity, CallbackInfoReturnable<Boolean> cir) {
		if (entity instanceof Player) {
			cir.setReturnValue(true);
		}
	}

	@Inject(method = "renderNameTag", at = @At("HEAD"), cancellable = true)
	private void renderGlowingNameTag(
			T entity,
			Component displayName,
			PoseStack poseStack,
			MultiBufferSource bufferSource,
			int packedLight,
			CallbackInfo ci
	) {
		if (!(entity instanceof Player)) return;

		ci.cancel();

		Font font = getFont();
		float nameWidth = font.width(displayName);
		float scale = 0.025f;

		poseStack.pushPose();

		poseStack.translate(0.0, 2.0, 0.0);
		poseStack.scale(scale, -scale, scale);

		Matrix4f mat = poseStack.last().pose();
		float x = -nameWidth / 2.0f;

		RenderSystem.disableDepthTest();
		font.drawInBatch(
				displayName, x, 0f,
				0xFFFFFF00,
				false, mat, bufferSource,
				Font.DisplayMode.SEE_THROUGH,
				0x20000000,
				packedLight
		);

		RenderSystem.enableDepthTest();
		font.drawInBatch(
				displayName, x, 0f,
				0xFFFFFF44,
				false, mat, bufferSource,
				Font.DisplayMode.NORMAL,
				0x40000000,
				packedLight
		);

		poseStack.popPose();
	}
}