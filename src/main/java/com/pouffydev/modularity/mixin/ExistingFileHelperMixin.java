package com.pouffydev.modularity.mixin;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ExistingFileHelper.class)
public class ExistingFileHelperMixin {

    @Inject(method = "exists(Lnet/minecraft/resources/ResourceLocation;Lnet/minecraft/server/packs/PackType;)Z", at = @At("HEAD"), cancellable = true)
    private void exists(ResourceLocation loc, PackType packType, CallbackInfoReturnable<Boolean> cir) {
        if (loc.getNamespace().equals("modularity")) {
            cir.setReturnValue(true);
        }
    }
}
