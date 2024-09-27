package bigchadguys.sellingbin.data.serializable;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public interface IDataSerializable {

	void writeData(DataOutput data) throws IOException;

	void readData(DataInput data) throws IOException;

}
