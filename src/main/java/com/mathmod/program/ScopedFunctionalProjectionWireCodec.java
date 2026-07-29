package com.mathmod.program;

import net.minecraft.network.RegistryFriendlyByteBuf;

/** Bounded menu-opening codec; it deliberately transports projection text, never source bytes. */
public final class ScopedFunctionalProjectionWireCodec {
    public static final int MAX_BYTES=65_536;
    private ScopedFunctionalProjectionWireCodec() { }
    public static void write(RegistryFriendlyByteBuf buffer, ScopedFunctionalProjection projection) {
        byte[] frame = encode(projection);
        buffer.writeVarInt(frame.length);
        buffer.writeBytes(frame);
    }
    /** Production menu writer: an overflow never copies the failed temporary frame. */
    public static void writeFailClosed(RegistryFriendlyByteBuf buffer, ScopedFunctionalProjection projection) {
        try {
            write(buffer, projection);
        } catch (IllegalArgumentException overflow) {
            write(buffer, ScopedFunctionalProjection.unavailable(projection.graphState()));
        }
    }
    static byte[] encode(ScopedFunctionalProjection projection) {
        io.netty.buffer.ByteBuf temporary=io.netty.buffer.Unpooled.buffer(256, MAX_BYTES);
        try {
            net.minecraft.network.FriendlyByteBuf frame=new net.minecraft.network.FriendlyByteBuf(temporary);
            writeFrame(frame,projection);
            int length=temporary.readableBytes(); if(length>MAX_BYTES) throw new IllegalArgumentException("projection frame limit");
            byte[] bytes = new byte[length];
            temporary.getBytes(temporary.readerIndex(), bytes);
            return bytes;
        } catch (IndexOutOfBoundsException exception) {
            throw new IllegalArgumentException("projection frame limit", exception);
        } finally { temporary.release(); }
    }
    public static ScopedFunctionalProjection read(RegistryFriendlyByteBuf buffer) {
        int length=buffer.readVarInt(); if(length<0||length>MAX_BYTES||buffer.readableBytes()<length)throw new IllegalArgumentException("projection frame limit");
        io.netty.buffer.ByteBuf bytes=buffer.readBytes(length);
        try {
            return decode(bytes);
        } finally { bytes.release(); }
    }
    static ScopedFunctionalProjection decode(io.netty.buffer.ByteBuf bytes) {
        net.minecraft.network.FriendlyByteBuf frame=new net.minecraft.network.FriendlyByteBuf(bytes);
        ScopedFunctionalProjection result=readFrame(frame); if(frame.isReadable())throw new IllegalArgumentException("projection trailing bytes"); return result;
    }
    private static void writeFrame(net.minecraft.network.FriendlyByteBuf b,ScopedFunctionalProjection p) { b.writeVarInt(p.schema()); enumValue(b,p.sourceState());enumValue(b,p.attemptState());enumValue(b,p.graphState());enumValue(b,p.graphRelation()); rows(b,p.authoredRows());rows(b,p.checkedRows());diagnostics(b,p.diagnostics());b.writeVarInt(p.chargedSteps()); }
    private static ScopedFunctionalProjection readFrame(net.minecraft.network.FriendlyByteBuf b) { int schema=b.readVarInt(); if(schema!=1)throw new IllegalArgumentException("projection schema"); return new ScopedFunctionalProjection(schema,readEnum(b,ScopedFunctionalProjection.SourceState.class),readEnum(b,ScopedFunctionalProjection.AttemptState.class),readEnum(b,ScopedFunctionalProjection.GraphState.class),readEnum(b,ScopedFunctionalProjection.GraphRelation.class),readRows(b),readRows(b),readDiagnostics(b),boundedInt(b,4096)); }
    private static void rows(net.minecraft.network.FriendlyByteBuf b,java.util.List<ScopedFunctionalProjection.Row> rows){b.writeVarInt(rows.size());for(var r:rows){text(b,r.structuralPath(),512);enumValue(b,r.kind());text(b,r.primaryToken(),256);text(b,r.secondaryToken(),256);b.writeVarInt(r.bindingIndex()+1);b.writeVarInt(r.depth());}}
    private static java.util.List<ScopedFunctionalProjection.Row> readRows(net.minecraft.network.FriendlyByteBuf b){int n=boundedInt(b,256);java.util.ArrayList<ScopedFunctionalProjection.Row> rows=new java.util.ArrayList<>(n);for(int i=0;i<n;i++)rows.add(new ScopedFunctionalProjection.Row(text(b,512),readEnum(b,ScopedFunctionalProjection.RowKind.class),text(b,256),text(b,256),boundedInt(b,16)-1,boundedInt(b,16)));return java.util.List.copyOf(rows);}
    private static void diagnostics(net.minecraft.network.FriendlyByteBuf b,java.util.List<ScopedFunctionalProjection.Diagnostic> values){b.writeVarInt(values.size());for(var d:values){enumValue(b,d.phase());enumValue(b,d.code());text(b,d.structuralPath(),512);}}
    private static java.util.List<ScopedFunctionalProjection.Diagnostic> readDiagnostics(net.minecraft.network.FriendlyByteBuf b){int n=boundedInt(b,256);java.util.ArrayList<ScopedFunctionalProjection.Diagnostic> values=new java.util.ArrayList<>(n);for(int i=0;i<n;i++)values.add(new ScopedFunctionalProjection.Diagnostic(readEnum(b,ScopedFunctionalProjection.Phase.class),readEnum(b,ScopedFunctionalProjection.Code.class),text(b,512)));return java.util.List.copyOf(values);}
    private static void text(net.minecraft.network.FriendlyByteBuf b,String value,int max){byte[] bytes=value.getBytes(java.nio.charset.StandardCharsets.UTF_8);if(bytes.length>max)throw new IllegalArgumentException("projection string limit");b.writeVarInt(bytes.length);b.writeBytes(bytes);}
    private static String text(net.minecraft.network.FriendlyByteBuf b,int max){int length=boundedInt(b,max);if(b.readableBytes()<length)throw new IllegalArgumentException("projection string truncated");return b.readCharSequence(length,java.nio.charset.StandardCharsets.UTF_8).toString();}
    private static void enumValue(net.minecraft.network.FriendlyByteBuf b,Enum<?> value){b.writeVarInt(value.ordinal());}
    private static <E extends Enum<E>> E readEnum(net.minecraft.network.FriendlyByteBuf b,Class<E> type){int ordinal=b.readVarInt();E[] values=type.getEnumConstants();if(ordinal<0||ordinal>=values.length)throw new IllegalArgumentException("projection enum");return values[ordinal];}
    private static int boundedInt(net.minecraft.network.FriendlyByteBuf b,int max){int value=b.readVarInt();if(value<0||value>max)throw new IllegalArgumentException("projection bound");return value;}
}
