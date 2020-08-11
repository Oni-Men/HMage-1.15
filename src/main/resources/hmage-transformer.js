function initializeCoreMod() {
    var ASMAPI = Java.type('net.minecraftforge.coremod.api.ASMAPI');
    var Opcodes = Java.type('org.objectweb.asm.Opcodes');
    var InsnList = Java.type('org.objectweb.asm.tree.InsnList');
    var InsnNode = Java.type('org.objectweb.asm.tree.InsnNode');
    var Type = Java.type('org.objectweb.asm.Type');
    var JumpInsnNode = Java.type('org.objectweb.asm.tree.JumpInsnNode');
    var MethodInsnNode = Java.type('org.objectweb.asm.tree.MethodInsnNode');
    var LabelNode = Java.type('org.objectweb.asm.tree.LabelNode');

	return {
		'hook-render-background': {
			'target': {
				'type': 'METHOD',
				'class': 'net.minecraft.client.gui.screen.Screen',
				'methodName': 'renderBackground',
				'methodDesc': '(I)V'
			},
			'transformer': function(method) {
				var injectings = new InsnList();
				var hookNode = new MethodInsnNode(Opcodes.INVOKESTATIC, 'onim/en/hmage/HMageHooks', 'onRenderBackground', '()Z');

				var returnNode = new InsnNode(Opcodes.RETURN);
				var gotoNode = new LabelNode();
				var ifeqNode = new JumpInsnNode(Opcodes.IFEQ, gotoNode);

				injectings.add(hookNode);
				injectings.add(ifeqNode);
				injectings.add(returnNode);
				injectings.add(gotoNode);

				method.instructions.insert(injectings);

				return method;
			}

		}
	}
}
