package bigchadguys.sellingbin.data.adapter;

import bigchadguys.sellingbin.data.bit.BitBuffer;

import java.util.Optional;

public interface IBitAdapter<T, C> {

    void writeBits(T value, BitBuffer buffer, C context);

    Optional<T> readBits(BitBuffer buffer, C context);

}
