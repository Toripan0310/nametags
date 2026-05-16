package com.github.toripan0310.nametags.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityRenderer.class)
public abstract class EntityRendererMixin<T extends Entity> {
	@Shadow @Final
	protected net.minecraft.client.renderer.entity.EntityRenderDispatcher entityRenderDispatcher;

	@Shadow
	public abstract Font getFont();

	// ネームタグ常時表示
	@Inject(method = "shouldShowName", at = @At("HEAD"), cancellable = true)
	private void alwaysShowName(T entity, CallbackInfoReturnable<Boolean> cir) {
		cir.setReturnValue(true);
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
		ci.cancel();

		double distSq = entityRenderDispatcher.distanceToSqr(entity);
		if (distSq > 4096.0) return;

		Font font = getFont();
		float nameWidth = font.width(displayName);
		float scale = 0.025f;

		poseStack.pushPose();

		// ★ 1. 頭の上に移動（BBの高さ + 少し上の余白）
		float yOffset = entity.getBbHeight() + 0.5f;
		poseStack.translate(0.0, yOffset, 0.0);

		// ★ 2. カメラの向きに合わせてビルボード回転
		Quaternionf cameraRotation = Minecraft.getInstance()
				.getEntityRenderDispatcher()
				.cameraOrientation();
		poseStack.mulPose(cameraRotation);

		// ★ 3. スケール（Y軸は反転して文字を正しい向きに）
		poseStack.scale(scale, -scale, scale);

		Matrix4f mat = poseStack.last().pose();
		float x = -nameWidth / 2.0f;

		// 壁貫通レイヤー（薄い黄色）
		RenderSystem.disableDepthTest();
		font.drawInBatch(
				displayName, x, 0f,
				0xAAFFAA00,
				false, mat, bufferSource,
				Font.DisplayMode.SEE_THROUGH,
				0x20000000,
				packedLight
		);

		// 通常レイヤー（明るい黄色）
		RenderSystem.enableDepthTest();
		font.drawInBatch(
				displayName, x, 0f,
				0xFFFFDD00,
				false, mat, bufferSource,
				Font.DisplayMode.NORMAL,
				0x40000000,
				packedLight
		);

		poseStack.popPose();
	}
}