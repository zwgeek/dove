package zg.dove.codec;

public class StringCodec implements ICodec<String, byte[]> {
    @Override
    public byte[] encode(String obj) {
        return obj.getBytes();
    }

    @Override
    public String decode(byte[] obj) {
        return new String(obj);
    }
}
