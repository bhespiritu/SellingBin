package bigchadguys.sellingbin.data.adapter;

import io.netty.buffer.ByteBuf;

import java.util.Optional;

public interface IByteAdapter<T, C> {

    void writeBytes(T value, ByteBuf buffer, C context);

    Optional<T> readBytes(ByteBuf buffer, C context);

}
