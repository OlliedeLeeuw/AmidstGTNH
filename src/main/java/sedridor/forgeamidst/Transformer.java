package sedridor.forgeamidst;

import java.util.Iterator;
import java.util.LinkedHashMap;

import net.minecraft.launchwrapper.IClassTransformer;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.VarInsnNode;

public class Transformer implements IClassTransformer {

    private static final LinkedHashMap<String, Boolean> classesPatched = new LinkedHashMap<String, Boolean>();

    private final String class1 = "climateControl.DimensionManager";

    public byte[] transform(String name, String newName, byte[] bytes) {
        if (newName.equals("climateControl.DimensionManager")
            && !classesPatched.containsKey("climateControl.DimensionManager")) {
            classesPatched.put("climateControl.DimensionManager", Boolean.valueOf(true));
            return transform1(bytes, !name.equals(newName));
        }
        return bytes;
    }

    private byte[] transform1(byte[] bytes, boolean obfuscated) {
        System.out.println("* Core transform running on DimensionManager *");
        ClassNode classN = new ClassNode();
        ClassReader classR = new ClassReader(bytes);
        classR.accept((ClassVisitor) classN, 0);
        Iterator<MethodNode> methods = classN.methods.iterator();
        int indexMethod = 0;
        while (methods.hasNext()) {
            MethodNode m = methods.next();
            if (m.name.equals("onCreateSpawn")
                && m.desc.equals("(Lnet/minecraftforge/event/world/WorldEvent$CreateSpawnPosition;)V")) {
                indexMethod++;
                AbstractInsnNode targetNode = null;
                Iterator<AbstractInsnNode> iter = m.instructions.iterator();
                int index = 0;
                while (iter.hasNext()) {
                    index++;
                    targetNode = iter.next();
                    if (targetNode.getOpcode() == 182 && index == 49) {
                        InsnList toInject = new InsnList();
                        LabelNode l8 = (LabelNode) m.instructions.get(index);
                        toInject.add(new VarInsnNode(25, 2));
                        toInject.add(new TypeInsnNode(193, "sedridor/forgeamidst/MapWorldRTG"));
                        toInject.add(new JumpInsnNode(153, l8));
                        toInject.add(new VarInsnNode(21, 3));
                        toInject.add(new JumpInsnNode(154, l8));
                        toInject.add(new VarInsnNode(25, 1));
                        toInject.add(
                            new FieldInsnNode(
                                180,
                                "net/minecraftforge/event/world/WorldEvent$CreateSpawnPosition",
                                "world",
                                "Lnet/minecraft/world/World;"));
                        toInject.add(new TypeInsnNode(192, "sedridor/forgeamidst/MapWorldRTG"));
                        toInject.add(new VarInsnNode(25, 0));
                        toInject.add(new InsnNode(3));
                        toInject.add(
                            new MethodInsnNode(
                                183,
                                "climateControl/DimensionManager",
                                "riverLayerWrapper",
                                "(I)LclimateControl/customGenLayer/GenLayerRiverMixWrapper;",
                                false));
                        toInject.add(
                            new MethodInsnNode(
                                182,
                                "sedridor/forgeamidst/MapWorldRTG",
                                "updateGenLayers",
                                "(LclimateControl/customGenLayer/GenLayerRiverMixWrapper;)V",
                                false));
                        m.instructions.insertBefore(m.instructions.get(index), toInject);
                        break;
                    }
                }
                break;
            }
        }
        ClassWriter writer = new ClassWriter(3);
        classN.accept((ClassVisitor) writer);
        return writer.toByteArray();
    }
}
