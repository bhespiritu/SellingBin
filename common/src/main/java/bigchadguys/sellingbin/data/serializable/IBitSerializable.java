package bigchadguys.sellingbin.data.serializable;

import bigchadguys.sellingbin.data.bit.BitBuffer;

public interface IBitSerializable {

	void writeBits(BitBuffer buffer);

	void readBits(BitBuffer buffer);

}
