package bigchadguys.sellingbin.data.adapter;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Optional;

public interface IDataAdapter<T, C> {

    void writeData(T value, DataOutput data, C context) throws IOException;

    Optional<T> readData(DataInput data, C context) throws IOException;

}
