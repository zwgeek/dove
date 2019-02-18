package zg.dove.codec;

public class HexStringCodec implements ICodec<String, byte[]> {

    private static final byte[] HEX_BYTES = new byte[]
            {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};


    private static byte toByte(final String toConvert, final int pos) {
        int response = Character.digit(toConvert.charAt(pos), 16);
        if (response < 0 || response > 15) {
            throw new IllegalArgumentException("Non-hex character '" + toConvert.charAt(pos) + "' at index=" + pos);
        }

        return (byte) response;
    }

    @Override
    public byte[] encode(String obj) {
        if ((obj.length() & 1) != 0) { //% 2
            throw new IllegalArgumentException("The supplied character array must contain an even number of hex chars.");
        }

        byte[] response = new byte[obj.length() >> 1]; /// 2

        for (int i = 0; i < response.length; i++) {
            int posOne = i << 1; //* 2
            response[i] = (byte) (toByte(obj, posOne) << 4 | toByte(obj, posOne + 1));
        }

        return response;
    }

    @Override
    public String decode(byte[] obj) {
        if (obj == null) {
            throw new NullPointerException("Parameter to be converted can not be null");
        }

        byte[] converted = new byte[obj.length * 2];
        for (int i = 0; i < obj.length; i++) {
            byte b = obj[i];
            converted[i * 2] = HEX_BYTES[b >> 4 & 0x0F];
            converted[i * 2 + 1] = HEX_BYTES[b & 0x0F];
        }

        return converted.toString();
    }
}
