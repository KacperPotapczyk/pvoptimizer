/**
 * Autogenerated by Avro
 *
 * DO NOT EDIT DIRECTLY
 */
package com.github.kacperpotapczyk.pvoptimizer.dto;

import org.apache.avro.generic.GenericArray;
import org.apache.avro.specific.SpecificData;
import org.apache.avro.util.Utf8;
import org.apache.avro.message.BinaryMessageEncoder;
import org.apache.avro.message.BinaryMessageDecoder;
import org.apache.avro.message.SchemaStore;

@org.apache.avro.specific.AvroGenerated
public class ProductionDto extends org.apache.avro.specific.SpecificRecordBase implements org.apache.avro.specific.SpecificRecord {
  private static final long serialVersionUID = -995264584286431868L;


  public static final org.apache.avro.Schema SCHEMA$ = new org.apache.avro.Schema.Parser().parse("{\"type\":\"record\",\"name\":\"ProductionDto\",\"namespace\":\"com.github.kacperpotapczyk.pvoptimizer.dto\",\"fields\":[{\"name\":\"id\",\"type\":\"long\",\"doc\":\"Production id\"},{\"name\":\"name\",\"type\":\"string\",\"doc\":\"Production name\"},{\"name\":\"productionProfile\",\"type\":{\"type\":\"array\",\"items\":\"double\"},\"doc\":\"Production profile\"}]}");
  public static org.apache.avro.Schema getClassSchema() { return SCHEMA$; }

  private static final SpecificData MODEL$ = new SpecificData();

  private static final BinaryMessageEncoder<ProductionDto> ENCODER =
      new BinaryMessageEncoder<>(MODEL$, SCHEMA$);

  private static final BinaryMessageDecoder<ProductionDto> DECODER =
      new BinaryMessageDecoder<>(MODEL$, SCHEMA$);

  /**
   * Return the BinaryMessageEncoder instance used by this class.
   * @return the message encoder used by this class
   */
  public static BinaryMessageEncoder<ProductionDto> getEncoder() {
    return ENCODER;
  }

  /**
   * Return the BinaryMessageDecoder instance used by this class.
   * @return the message decoder used by this class
   */
  public static BinaryMessageDecoder<ProductionDto> getDecoder() {
    return DECODER;
  }

  /**
   * Create a new BinaryMessageDecoder instance for this class that uses the specified {@link SchemaStore}.
   * @param resolver a {@link SchemaStore} used to find schemas by fingerprint
   * @return a BinaryMessageDecoder instance for this class backed by the given SchemaStore
   */
  public static BinaryMessageDecoder<ProductionDto> createDecoder(SchemaStore resolver) {
    return new BinaryMessageDecoder<>(MODEL$, SCHEMA$, resolver);
  }

  /**
   * Serializes this ProductionDto to a ByteBuffer.
   * @return a buffer holding the serialized data for this instance
   * @throws java.io.IOException if this instance could not be serialized
   */
  public java.nio.ByteBuffer toByteBuffer() throws java.io.IOException {
    return ENCODER.encode(this);
  }

  /**
   * Deserializes a ProductionDto from a ByteBuffer.
   * @param b a byte buffer holding serialized data for an instance of this class
   * @return a ProductionDto instance decoded from the given buffer
   * @throws java.io.IOException if the given bytes could not be deserialized into an instance of this class
   */
  public static ProductionDto fromByteBuffer(
      java.nio.ByteBuffer b) throws java.io.IOException {
    return DECODER.decode(b);
  }

  /** Production id */
  private long id;
  /** Production name */
  private java.lang.CharSequence name;
  /** Production profile */
  private java.util.List<java.lang.Double> productionProfile;

  /**
   * Default constructor.  Note that this does not initialize fields
   * to their default values from the schema.  If that is desired then
   * one should use <code>newBuilder()</code>.
   */
  public ProductionDto() {}

  /**
   * All-args constructor.
   * @param id Production id
   * @param name Production name
   * @param productionProfile Production profile
   */
  public ProductionDto(java.lang.Long id, java.lang.CharSequence name, java.util.List<java.lang.Double> productionProfile) {
    this.id = id;
    this.name = name;
    this.productionProfile = productionProfile;
  }

  @Override
  public org.apache.avro.specific.SpecificData getSpecificData() { return MODEL$; }

  @Override
  public org.apache.avro.Schema getSchema() { return SCHEMA$; }

  // Used by DatumWriter.  Applications should not call.
  @Override
  public java.lang.Object get(int field$) {
    switch (field$) {
    case 0: return id;
    case 1: return name;
    case 2: return productionProfile;
    default: throw new IndexOutOfBoundsException("Invalid index: " + field$);
    }
  }

  // Used by DatumReader.  Applications should not call.
  @Override
  @SuppressWarnings(value="unchecked")
  public void put(int field$, java.lang.Object value$) {
    switch (field$) {
    case 0: id = (java.lang.Long)value$; break;
    case 1: name = (java.lang.CharSequence)value$; break;
    case 2: productionProfile = (java.util.List<java.lang.Double>)value$; break;
    default: throw new IndexOutOfBoundsException("Invalid index: " + field$);
    }
  }

  /**
   * Gets the value of the 'id' field.
   * @return Production id
   */
  public long getId() {
    return id;
  }


  /**
   * Sets the value of the 'id' field.
   * Production id
   * @param value the value to set.
   */
  public void setId(long value) {
    this.id = value;
  }

  /**
   * Gets the value of the 'name' field.
   * @return Production name
   */
  public java.lang.CharSequence getName() {
    return name;
  }


  /**
   * Sets the value of the 'name' field.
   * Production name
   * @param value the value to set.
   */
  public void setName(java.lang.CharSequence value) {
    this.name = value;
  }

  /**
   * Gets the value of the 'productionProfile' field.
   * @return Production profile
   */
  public java.util.List<java.lang.Double> getProductionProfile() {
    return productionProfile;
  }


  /**
   * Sets the value of the 'productionProfile' field.
   * Production profile
   * @param value the value to set.
   */
  public void setProductionProfile(java.util.List<java.lang.Double> value) {
    this.productionProfile = value;
  }

  /**
   * Creates a new ProductionDto RecordBuilder.
   * @return A new ProductionDto RecordBuilder
   */
  public static com.github.kacperpotapczyk.pvoptimizer.dto.ProductionDto.Builder newBuilder() {
    return new com.github.kacperpotapczyk.pvoptimizer.dto.ProductionDto.Builder();
  }

  /**
   * Creates a new ProductionDto RecordBuilder by copying an existing Builder.
   * @param other The existing builder to copy.
   * @return A new ProductionDto RecordBuilder
   */
  public static com.github.kacperpotapczyk.pvoptimizer.dto.ProductionDto.Builder newBuilder(com.github.kacperpotapczyk.pvoptimizer.dto.ProductionDto.Builder other) {
    if (other == null) {
      return new com.github.kacperpotapczyk.pvoptimizer.dto.ProductionDto.Builder();
    } else {
      return new com.github.kacperpotapczyk.pvoptimizer.dto.ProductionDto.Builder(other);
    }
  }

  /**
   * Creates a new ProductionDto RecordBuilder by copying an existing ProductionDto instance.
   * @param other The existing instance to copy.
   * @return A new ProductionDto RecordBuilder
   */
  public static com.github.kacperpotapczyk.pvoptimizer.dto.ProductionDto.Builder newBuilder(com.github.kacperpotapczyk.pvoptimizer.dto.ProductionDto other) {
    if (other == null) {
      return new com.github.kacperpotapczyk.pvoptimizer.dto.ProductionDto.Builder();
    } else {
      return new com.github.kacperpotapczyk.pvoptimizer.dto.ProductionDto.Builder(other);
    }
  }

  /**
   * RecordBuilder for ProductionDto instances.
   */
  @org.apache.avro.specific.AvroGenerated
  public static class Builder extends org.apache.avro.specific.SpecificRecordBuilderBase<ProductionDto>
    implements org.apache.avro.data.RecordBuilder<ProductionDto> {

    /** Production id */
    private long id;
    /** Production name */
    private java.lang.CharSequence name;
    /** Production profile */
    private java.util.List<java.lang.Double> productionProfile;

    /** Creates a new Builder */
    private Builder() {
      super(SCHEMA$, MODEL$);
    }

    /**
     * Creates a Builder by copying an existing Builder.
     * @param other The existing Builder to copy.
     */
    private Builder(com.github.kacperpotapczyk.pvoptimizer.dto.ProductionDto.Builder other) {
      super(other);
      if (isValidValue(fields()[0], other.id)) {
        this.id = data().deepCopy(fields()[0].schema(), other.id);
        fieldSetFlags()[0] = other.fieldSetFlags()[0];
      }
      if (isValidValue(fields()[1], other.name)) {
        this.name = data().deepCopy(fields()[1].schema(), other.name);
        fieldSetFlags()[1] = other.fieldSetFlags()[1];
      }
      if (isValidValue(fields()[2], other.productionProfile)) {
        this.productionProfile = data().deepCopy(fields()[2].schema(), other.productionProfile);
        fieldSetFlags()[2] = other.fieldSetFlags()[2];
      }
    }

    /**
     * Creates a Builder by copying an existing ProductionDto instance
     * @param other The existing instance to copy.
     */
    private Builder(com.github.kacperpotapczyk.pvoptimizer.dto.ProductionDto other) {
      super(SCHEMA$, MODEL$);
      if (isValidValue(fields()[0], other.id)) {
        this.id = data().deepCopy(fields()[0].schema(), other.id);
        fieldSetFlags()[0] = true;
      }
      if (isValidValue(fields()[1], other.name)) {
        this.name = data().deepCopy(fields()[1].schema(), other.name);
        fieldSetFlags()[1] = true;
      }
      if (isValidValue(fields()[2], other.productionProfile)) {
        this.productionProfile = data().deepCopy(fields()[2].schema(), other.productionProfile);
        fieldSetFlags()[2] = true;
      }
    }

    /**
      * Gets the value of the 'id' field.
      * Production id
      * @return The value.
      */
    public long getId() {
      return id;
    }


    /**
      * Sets the value of the 'id' field.
      * Production id
      * @param value The value of 'id'.
      * @return This builder.
      */
    public com.github.kacperpotapczyk.pvoptimizer.dto.ProductionDto.Builder setId(long value) {
      validate(fields()[0], value);
      this.id = value;
      fieldSetFlags()[0] = true;
      return this;
    }

    /**
      * Checks whether the 'id' field has been set.
      * Production id
      * @return True if the 'id' field has been set, false otherwise.
      */
    public boolean hasId() {
      return fieldSetFlags()[0];
    }


    /**
      * Clears the value of the 'id' field.
      * Production id
      * @return This builder.
      */
    public com.github.kacperpotapczyk.pvoptimizer.dto.ProductionDto.Builder clearId() {
      fieldSetFlags()[0] = false;
      return this;
    }

    /**
      * Gets the value of the 'name' field.
      * Production name
      * @return The value.
      */
    public java.lang.CharSequence getName() {
      return name;
    }


    /**
      * Sets the value of the 'name' field.
      * Production name
      * @param value The value of 'name'.
      * @return This builder.
      */
    public com.github.kacperpotapczyk.pvoptimizer.dto.ProductionDto.Builder setName(java.lang.CharSequence value) {
      validate(fields()[1], value);
      this.name = value;
      fieldSetFlags()[1] = true;
      return this;
    }

    /**
      * Checks whether the 'name' field has been set.
      * Production name
      * @return True if the 'name' field has been set, false otherwise.
      */
    public boolean hasName() {
      return fieldSetFlags()[1];
    }


    /**
      * Clears the value of the 'name' field.
      * Production name
      * @return This builder.
      */
    public com.github.kacperpotapczyk.pvoptimizer.dto.ProductionDto.Builder clearName() {
      name = null;
      fieldSetFlags()[1] = false;
      return this;
    }

    /**
      * Gets the value of the 'productionProfile' field.
      * Production profile
      * @return The value.
      */
    public java.util.List<java.lang.Double> getProductionProfile() {
      return productionProfile;
    }


    /**
      * Sets the value of the 'productionProfile' field.
      * Production profile
      * @param value The value of 'productionProfile'.
      * @return This builder.
      */
    public com.github.kacperpotapczyk.pvoptimizer.dto.ProductionDto.Builder setProductionProfile(java.util.List<java.lang.Double> value) {
      validate(fields()[2], value);
      this.productionProfile = value;
      fieldSetFlags()[2] = true;
      return this;
    }

    /**
      * Checks whether the 'productionProfile' field has been set.
      * Production profile
      * @return True if the 'productionProfile' field has been set, false otherwise.
      */
    public boolean hasProductionProfile() {
      return fieldSetFlags()[2];
    }


    /**
      * Clears the value of the 'productionProfile' field.
      * Production profile
      * @return This builder.
      */
    public com.github.kacperpotapczyk.pvoptimizer.dto.ProductionDto.Builder clearProductionProfile() {
      productionProfile = null;
      fieldSetFlags()[2] = false;
      return this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public ProductionDto build() {
      try {
        ProductionDto record = new ProductionDto();
        record.id = fieldSetFlags()[0] ? this.id : (java.lang.Long) defaultValue(fields()[0]);
        record.name = fieldSetFlags()[1] ? this.name : (java.lang.CharSequence) defaultValue(fields()[1]);
        record.productionProfile = fieldSetFlags()[2] ? this.productionProfile : (java.util.List<java.lang.Double>) defaultValue(fields()[2]);
        return record;
      } catch (org.apache.avro.AvroMissingFieldException e) {
        throw e;
      } catch (java.lang.Exception e) {
        throw new org.apache.avro.AvroRuntimeException(e);
      }
    }
  }

  @SuppressWarnings("unchecked")
  private static final org.apache.avro.io.DatumWriter<ProductionDto>
    WRITER$ = (org.apache.avro.io.DatumWriter<ProductionDto>)MODEL$.createDatumWriter(SCHEMA$);

  @Override public void writeExternal(java.io.ObjectOutput out)
    throws java.io.IOException {
    WRITER$.write(this, SpecificData.getEncoder(out));
  }

  @SuppressWarnings("unchecked")
  private static final org.apache.avro.io.DatumReader<ProductionDto>
    READER$ = (org.apache.avro.io.DatumReader<ProductionDto>)MODEL$.createDatumReader(SCHEMA$);

  @Override public void readExternal(java.io.ObjectInput in)
    throws java.io.IOException {
    READER$.read(this, SpecificData.getDecoder(in));
  }

  @Override protected boolean hasCustomCoders() { return true; }

  @Override public void customEncode(org.apache.avro.io.Encoder out)
    throws java.io.IOException
  {
    out.writeLong(this.id);

    out.writeString(this.name);

    long size0 = this.productionProfile.size();
    out.writeArrayStart();
    out.setItemCount(size0);
    long actualSize0 = 0;
    for (java.lang.Double e0: this.productionProfile) {
      actualSize0++;
      out.startItem();
      out.writeDouble(e0);
    }
    out.writeArrayEnd();
    if (actualSize0 != size0)
      throw new java.util.ConcurrentModificationException("Array-size written was " + size0 + ", but element count was " + actualSize0 + ".");

  }

  @Override public void customDecode(org.apache.avro.io.ResolvingDecoder in)
    throws java.io.IOException
  {
    org.apache.avro.Schema.Field[] fieldOrder = in.readFieldOrderIfDiff();
    if (fieldOrder == null) {
      this.id = in.readLong();

      this.name = in.readString(this.name instanceof Utf8 ? (Utf8)this.name : null);

      long size0 = in.readArrayStart();
      java.util.List<java.lang.Double> a0 = this.productionProfile;
      if (a0 == null) {
        a0 = new SpecificData.Array<java.lang.Double>((int)size0, SCHEMA$.getField("productionProfile").schema());
        this.productionProfile = a0;
      } else a0.clear();
      SpecificData.Array<java.lang.Double> ga0 = (a0 instanceof SpecificData.Array ? (SpecificData.Array<java.lang.Double>)a0 : null);
      for ( ; 0 < size0; size0 = in.arrayNext()) {
        for ( ; size0 != 0; size0--) {
          java.lang.Double e0 = (ga0 != null ? ga0.peek() : null);
          e0 = in.readDouble();
          a0.add(e0);
        }
      }

    } else {
      for (int i = 0; i < 3; i++) {
        switch (fieldOrder[i].pos()) {
        case 0:
          this.id = in.readLong();
          break;

        case 1:
          this.name = in.readString(this.name instanceof Utf8 ? (Utf8)this.name : null);
          break;

        case 2:
          long size0 = in.readArrayStart();
          java.util.List<java.lang.Double> a0 = this.productionProfile;
          if (a0 == null) {
            a0 = new SpecificData.Array<java.lang.Double>((int)size0, SCHEMA$.getField("productionProfile").schema());
            this.productionProfile = a0;
          } else a0.clear();
          SpecificData.Array<java.lang.Double> ga0 = (a0 instanceof SpecificData.Array ? (SpecificData.Array<java.lang.Double>)a0 : null);
          for ( ; 0 < size0; size0 = in.arrayNext()) {
            for ( ; size0 != 0; size0--) {
              java.lang.Double e0 = (ga0 != null ? ga0.peek() : null);
              e0 = in.readDouble();
              a0.add(e0);
            }
          }
          break;

        default:
          throw new java.io.IOException("Corrupt ResolvingDecoder.");
        }
      }
    }
  }
}










