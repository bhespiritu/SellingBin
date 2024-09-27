package bigchadguys.sellingbin.data.adapter.number;

import bigchadguys.sellingbin.data.adapter.ISimpleAdapter;
import bigchadguys.sellingbin.data.bit.BitBuffer;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.*;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Optional;

public abstract class NumberAdapter<N extends Number> implements ISimpleAdapter<N, NbtElement, JsonElement> {

    private final boolean nullable;

    public NumberAdapter(boolean nullable) {
        this.nullable = nullable;
    }

    public boolean isNullable() {
        return this.nullable;
    }

    protected abstract void writeNumberBits(N value, BitBuffer buffer);

    protected abstract N readNumberBits(BitBuffer buffer);

    protected abstract void writeNumberBytes(N value, ByteBuf buffer);

    protected abstract N readNumberBytes(ByteBuf buffer);

    protected abstract void writeNumberData(N value, DataOutput data) throws IOException;

    protected abstract N readNumberData(DataInput data) throws IOException;

    protected abstract NbtElement writeNumberNbt(N value);

    protected abstract N readNumberNbt(NbtElement nbt);

    protected abstract JsonElement writeNumberJson(N value);

    protected abstract N readNumberJson(JsonElement json);

    @Override
    public final void writeBits(N value, BitBuffer buffer) {
        if(this.nullable) {
            buffer.writeBoolean(value == null);
        }

        if(value != null) {
            this.writeNumberBits(value, buffer);
        }
    }

    @Override
    public final Optional<N> readBits(BitBuffer buffer) {
        if(this.nullable && buffer.readBoolean()) {
            return Optional.empty();
        }

        return Optional.of(this.readNumberBits(buffer));
    }

    @Override
    public final void writeBytes(N value, ByteBuf buffer) {
        if(this.nullable) {
            buffer.writeBoolean(value == null);
        }

        if(value != null) {
            this.writeNumberBytes(value, buffer);
        }
    }

    @Override
    public final Optional<N> readBytes(ByteBuf buffer) {
        if(this.nullable && buffer.readBoolean()) {
            return Optional.empty();
        }

        return Optional.of(this.readNumberBytes(buffer));
    }

    @Override
    public void writeData(N value, DataOutput data) throws IOException {
        if(this.nullable) {
            data.writeBoolean(value == null);
        }

        if(value != null) {
            this.writeNumberData(value, data);
        }
    }

    @Override
    public Optional<N> readData(DataInput data) throws IOException {
        if(this.nullable && data.readBoolean()) {
            return Optional.empty();
        }

        return Optional.of(this.readNumberData(data));
    }

    @Override
    public final Optional<NbtElement> writeNbt(N value) {
        return value == null ? Optional.empty() : Optional.ofNullable(this.writeNumberNbt(value));
    }

    @Override
    public final Optional<N> readNbt(NbtElement nbt) {
        return nbt == null ? Optional.empty() : Optional.ofNullable(this.readNumberNbt(nbt));
    }

    @Override
    public final Optional<JsonElement> writeJson(N value) {
        return value == null ? Optional.empty() : Optional.ofNullable(this.writeNumberJson(value));
    }

    @Override
    public final Optional<N> readJson(JsonElement json) {
        return json == null || json instanceof JsonNull ? Optional.empty() : Optional.ofNullable(this.readNumberJson(json));
    }

    public static Optional<Number> parse(String string) {
        try {
            if(string.contains(".") || string.contains("e") || string.contains("E")) {
                return Optional.ofNullable(reduce(new BigDecimal(string)));
            } else {
                return Optional.ofNullable(reduce(switch(string.length() < 2 ? "" : string.substring(0, 2)) {
                    case "0x" -> new BigInteger(string.substring(2), 16);
                    case "0o" -> new BigInteger(string.substring(2), 8);
                    case "0b" -> new BigInteger(string.substring(2), 2);
                    default -> new BigInteger(string, 10);
                }));
            }
        } catch(NumberFormatException exception) {
            return Optional.empty();
        }
    }

    public static Number reduce(Number number) {
        if(number instanceof Byte) {
            return number;
        } else if(number instanceof Short) {
            if(number.shortValue() == number.byteValue()) return number.byteValue();
            return number;
        } else if(number instanceof Integer) {
            if(number.intValue() == number.byteValue()) return number.byteValue();
            if(number.intValue() == number.shortValue()) return number.shortValue();
            return number;
        } else if(number instanceof Float) {
            if(number.floatValue() == number.byteValue()) return number.byteValue();
            if(number.floatValue() == number.shortValue()) return number.shortValue();
            return number;
        } else if(number instanceof Long) {
            if(number.longValue() == number.byteValue()) return number.byteValue();
            if(number.longValue() == number.shortValue()) return number.shortValue();
            if(number.longValue() == number.intValue()) return number.intValue();
            return number;
        } else if(number instanceof Double) {
            if(number.doubleValue() == number.byteValue()) return number.byteValue();
            if(number.doubleValue() == number.shortValue()) return number.shortValue();
            if(number.doubleValue() == number.intValue()) return number.intValue();
            if(number.doubleValue() == number.floatValue()) return number.floatValue();
            return number;
        } else if(number instanceof BigInteger integer) {
            if(integer.bitLength() <= 63) {
                return reduce(integer.longValueExact());
            }

            return number;
        } else if(number instanceof BigDecimal decimal) {
            if(decimal.stripTrailingZeros().scale() <= 0) {
                return reduce(decimal.toBigIntegerExact());
            } else if(BigDecimal.valueOf(decimal.doubleValue()).compareTo(decimal) == 0) {
                return reduce(decimal.doubleValue());
            }

            return number;
        }

        return number;
    }

    public static NbtElement wrap(Number number) {
        if(number instanceof Byte value) {
            return NbtByte.of(value);
        } else if(number instanceof Short value) {
            return NbtShort.of(value);
        } else if(number instanceof Integer value) {
            return NbtInt.of(value);
        } else if(number instanceof Float value) {
            return NbtFloat.of(value);
        } else if(number instanceof Long value) {
            return NbtLong.of(value);
        } else if(number instanceof Double value) {
            return NbtDouble.of(value);
        } else if(number instanceof BigInteger value) {
            return new NbtByteArray(value.toByteArray());
        } else if(number instanceof BigDecimal value) {
            return NbtString.of(value.toString());
        }

        return null;
    }

}
