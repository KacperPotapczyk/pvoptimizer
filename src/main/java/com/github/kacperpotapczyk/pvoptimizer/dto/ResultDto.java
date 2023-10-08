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
public class ResultDto extends org.apache.avro.specific.SpecificRecordBase implements org.apache.avro.specific.SpecificRecord {
  private static final long serialVersionUID = 2286237355307609658L;


  public static final org.apache.avro.Schema SCHEMA$ = new org.apache.avro.Schema.Parser().parse("{\"type\":\"record\",\"name\":\"ResultDto\",\"namespace\":\"com.github.kacperpotapczyk.pvoptimizer.dto\",\"fields\":[{\"name\":\"id\",\"type\":\"long\",\"doc\":\"Task identifier\"},{\"name\":\"optimizationStatus\",\"type\":{\"type\":\"enum\",\"name\":\"OptimizationStatusDto\",\"symbols\":[\"SOLUTION_FOUND\",\"SOLUTION_NOT_FOUND\"]}},{\"name\":\"objectiveFunctionValue\",\"type\":\"double\",\"doc\":\"Objective function optimal value\"},{\"name\":\"relativeGap\",\"type\":\"double\",\"doc\":\"Relative gap between relaxed solution and returned integer solution\"},{\"name\":\"elapsedTime\",\"type\":\"double\",\"doc\":\"Optimization elapsed time\"},{\"name\":\"errorMessage\",\"type\":\"string\",\"doc\":\"Error messages returned by optimizer\",\"default\":\"\"},{\"name\":\"contractResults\",\"type\":{\"type\":\"array\",\"items\":[{\"type\":\"record\",\"name\":\"ContractResultDto\",\"fields\":[{\"name\":\"id\",\"type\":\"long\",\"doc\":\"Contract id\"},{\"name\":\"name\",\"type\":\"string\",\"doc\":\"Contract name\"},{\"name\":\"power\",\"type\":{\"type\":\"array\",\"items\":[\"double\"]},\"doc\":\"Result power profile\"},{\"name\":\"energy\",\"type\":{\"type\":\"array\",\"items\":[\"double\"]},\"doc\":\"Result energy profile\"},{\"name\":\"cost\",\"type\":{\"type\":\"array\",\"items\":[\"double\"]},\"doc\":\"Result cost/income profile\"}]}]},\"default\":[]},{\"name\":\"storageResults\",\"type\":{\"type\":\"array\",\"items\":[{\"type\":\"record\",\"name\":\"StorageResultDto\",\"fields\":[{\"name\":\"id\",\"type\":\"long\",\"doc\":\"Storage id\"},{\"name\":\"name\",\"type\":\"string\",\"doc\":\"Storage name\"},{\"name\":\"charge\",\"type\":{\"type\":\"array\",\"items\":[\"double\"]},\"doc\":\"Storage charging profile\"},{\"name\":\"discharge\",\"type\":{\"type\":\"array\",\"items\":[\"double\"]},\"doc\":\"Storage discharging profile\"},{\"name\":\"energy\",\"type\":{\"type\":\"array\",\"items\":[\"double\"]},\"doc\":\"Storage storage energy profile\"},{\"name\":\"storageMode\",\"type\":{\"type\":\"array\",\"items\":[{\"type\":\"enum\",\"name\":\"StorageModeDto\",\"symbols\":[\"DISABLED\",\"CHARGING\",\"DISCHARGING\"]}]},\"doc\":\"Storage mode profile\"}]}]},\"default\":[]},{\"name\":\"movableDemandResults\",\"type\":{\"type\":\"array\",\"items\":[{\"type\":\"record\",\"name\":\"MovableDemandResultDto\",\"fields\":[{\"name\":\"id\",\"type\":\"long\",\"doc\":\"Movable demand id\"},{\"name\":\"name\",\"type\":\"string\",\"doc\":\"Movable demand name\"},{\"name\":\"startInterval\",\"type\":\"int\",\"doc\":\"Optimal start interval\"}]}]},\"default\":[]}]}");
  public static org.apache.avro.Schema getClassSchema() { return SCHEMA$; }

  private static final SpecificData MODEL$ = new SpecificData();

  private static final BinaryMessageEncoder<ResultDto> ENCODER =
      new BinaryMessageEncoder<>(MODEL$, SCHEMA$);

  private static final BinaryMessageDecoder<ResultDto> DECODER =
      new BinaryMessageDecoder<>(MODEL$, SCHEMA$);

  /**
   * Return the BinaryMessageEncoder instance used by this class.
   * @return the message encoder used by this class
   */
  public static BinaryMessageEncoder<ResultDto> getEncoder() {
    return ENCODER;
  }

  /**
   * Return the BinaryMessageDecoder instance used by this class.
   * @return the message decoder used by this class
   */
  public static BinaryMessageDecoder<ResultDto> getDecoder() {
    return DECODER;
  }

  /**
   * Create a new BinaryMessageDecoder instance for this class that uses the specified {@link SchemaStore}.
   * @param resolver a {@link SchemaStore} used to find schemas by fingerprint
   * @return a BinaryMessageDecoder instance for this class backed by the given SchemaStore
   */
  public static BinaryMessageDecoder<ResultDto> createDecoder(SchemaStore resolver) {
    return new BinaryMessageDecoder<>(MODEL$, SCHEMA$, resolver);
  }

  /**
   * Serializes this ResultDto to a ByteBuffer.
   * @return a buffer holding the serialized data for this instance
   * @throws java.io.IOException if this instance could not be serialized
   */
  public java.nio.ByteBuffer toByteBuffer() throws java.io.IOException {
    return ENCODER.encode(this);
  }

  /**
   * Deserializes a ResultDto from a ByteBuffer.
   * @param b a byte buffer holding serialized data for an instance of this class
   * @return a ResultDto instance decoded from the given buffer
   * @throws java.io.IOException if the given bytes could not be deserialized into an instance of this class
   */
  public static ResultDto fromByteBuffer(
      java.nio.ByteBuffer b) throws java.io.IOException {
    return DECODER.decode(b);
  }

  /** Task identifier */
  private long id;
  private com.github.kacperpotapczyk.pvoptimizer.dto.OptimizationStatusDto optimizationStatus;
  /** Objective function optimal value */
  private double objectiveFunctionValue;
  /** Relative gap between relaxed solution and returned integer solution */
  private double relativeGap;
  /** Optimization elapsed time */
  private double elapsedTime;
  /** Error messages returned by optimizer */
  private java.lang.CharSequence errorMessage;
  private java.util.List<java.lang.Object> contractResults;
  private java.util.List<java.lang.Object> storageResults;
  private java.util.List<java.lang.Object> movableDemandResults;

  /**
   * Default constructor.  Note that this does not initialize fields
   * to their default values from the schema.  If that is desired then
   * one should use <code>newBuilder()</code>.
   */
  public ResultDto() {}

  /**
   * All-args constructor.
   * @param id Task identifier
   * @param optimizationStatus The new value for optimizationStatus
   * @param objectiveFunctionValue Objective function optimal value
   * @param relativeGap Relative gap between relaxed solution and returned integer solution
   * @param elapsedTime Optimization elapsed time
   * @param errorMessage Error messages returned by optimizer
   * @param contractResults The new value for contractResults
   * @param storageResults The new value for storageResults
   * @param movableDemandResults The new value for movableDemandResults
   */
  public ResultDto(java.lang.Long id, com.github.kacperpotapczyk.pvoptimizer.dto.OptimizationStatusDto optimizationStatus, java.lang.Double objectiveFunctionValue, java.lang.Double relativeGap, java.lang.Double elapsedTime, java.lang.CharSequence errorMessage, java.util.List<java.lang.Object> contractResults, java.util.List<java.lang.Object> storageResults, java.util.List<java.lang.Object> movableDemandResults) {
    this.id = id;
    this.optimizationStatus = optimizationStatus;
    this.objectiveFunctionValue = objectiveFunctionValue;
    this.relativeGap = relativeGap;
    this.elapsedTime = elapsedTime;
    this.errorMessage = errorMessage;
    this.contractResults = contractResults;
    this.storageResults = storageResults;
    this.movableDemandResults = movableDemandResults;
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
    case 1: return optimizationStatus;
    case 2: return objectiveFunctionValue;
    case 3: return relativeGap;
    case 4: return elapsedTime;
    case 5: return errorMessage;
    case 6: return contractResults;
    case 7: return storageResults;
    case 8: return movableDemandResults;
    default: throw new IndexOutOfBoundsException("Invalid index: " + field$);
    }
  }

  // Used by DatumReader.  Applications should not call.
  @Override
  @SuppressWarnings(value="unchecked")
  public void put(int field$, java.lang.Object value$) {
    switch (field$) {
    case 0: id = (java.lang.Long)value$; break;
    case 1: optimizationStatus = (com.github.kacperpotapczyk.pvoptimizer.dto.OptimizationStatusDto)value$; break;
    case 2: objectiveFunctionValue = (java.lang.Double)value$; break;
    case 3: relativeGap = (java.lang.Double)value$; break;
    case 4: elapsedTime = (java.lang.Double)value$; break;
    case 5: errorMessage = (java.lang.CharSequence)value$; break;
    case 6: contractResults = (java.util.List<java.lang.Object>)value$; break;
    case 7: storageResults = (java.util.List<java.lang.Object>)value$; break;
    case 8: movableDemandResults = (java.util.List<java.lang.Object>)value$; break;
    default: throw new IndexOutOfBoundsException("Invalid index: " + field$);
    }
  }

  /**
   * Gets the value of the 'id' field.
   * @return Task identifier
   */
  public long getId() {
    return id;
  }


  /**
   * Sets the value of the 'id' field.
   * Task identifier
   * @param value the value to set.
   */
  public void setId(long value) {
    this.id = value;
  }

  /**
   * Gets the value of the 'optimizationStatus' field.
   * @return The value of the 'optimizationStatus' field.
   */
  public com.github.kacperpotapczyk.pvoptimizer.dto.OptimizationStatusDto getOptimizationStatus() {
    return optimizationStatus;
  }


  /**
   * Sets the value of the 'optimizationStatus' field.
   * @param value the value to set.
   */
  public void setOptimizationStatus(com.github.kacperpotapczyk.pvoptimizer.dto.OptimizationStatusDto value) {
    this.optimizationStatus = value;
  }

  /**
   * Gets the value of the 'objectiveFunctionValue' field.
   * @return Objective function optimal value
   */
  public double getObjectiveFunctionValue() {
    return objectiveFunctionValue;
  }


  /**
   * Sets the value of the 'objectiveFunctionValue' field.
   * Objective function optimal value
   * @param value the value to set.
   */
  public void setObjectiveFunctionValue(double value) {
    this.objectiveFunctionValue = value;
  }

  /**
   * Gets the value of the 'relativeGap' field.
   * @return Relative gap between relaxed solution and returned integer solution
   */
  public double getRelativeGap() {
    return relativeGap;
  }


  /**
   * Sets the value of the 'relativeGap' field.
   * Relative gap between relaxed solution and returned integer solution
   * @param value the value to set.
   */
  public void setRelativeGap(double value) {
    this.relativeGap = value;
  }

  /**
   * Gets the value of the 'elapsedTime' field.
   * @return Optimization elapsed time
   */
  public double getElapsedTime() {
    return elapsedTime;
  }


  /**
   * Sets the value of the 'elapsedTime' field.
   * Optimization elapsed time
   * @param value the value to set.
   */
  public void setElapsedTime(double value) {
    this.elapsedTime = value;
  }

  /**
   * Gets the value of the 'errorMessage' field.
   * @return Error messages returned by optimizer
   */
  public java.lang.CharSequence getErrorMessage() {
    return errorMessage;
  }


  /**
   * Sets the value of the 'errorMessage' field.
   * Error messages returned by optimizer
   * @param value the value to set.
   */
  public void setErrorMessage(java.lang.CharSequence value) {
    this.errorMessage = value;
  }

  /**
   * Gets the value of the 'contractResults' field.
   * @return The value of the 'contractResults' field.
   */
  public java.util.List<java.lang.Object> getContractResults() {
    return contractResults;
  }


  /**
   * Sets the value of the 'contractResults' field.
   * @param value the value to set.
   */
  public void setContractResults(java.util.List<java.lang.Object> value) {
    this.contractResults = value;
  }

  /**
   * Gets the value of the 'storageResults' field.
   * @return The value of the 'storageResults' field.
   */
  public java.util.List<java.lang.Object> getStorageResults() {
    return storageResults;
  }


  /**
   * Sets the value of the 'storageResults' field.
   * @param value the value to set.
   */
  public void setStorageResults(java.util.List<java.lang.Object> value) {
    this.storageResults = value;
  }

  /**
   * Gets the value of the 'movableDemandResults' field.
   * @return The value of the 'movableDemandResults' field.
   */
  public java.util.List<java.lang.Object> getMovableDemandResults() {
    return movableDemandResults;
  }


  /**
   * Sets the value of the 'movableDemandResults' field.
   * @param value the value to set.
   */
  public void setMovableDemandResults(java.util.List<java.lang.Object> value) {
    this.movableDemandResults = value;
  }

  /**
   * Creates a new ResultDto RecordBuilder.
   * @return A new ResultDto RecordBuilder
   */
  public static com.github.kacperpotapczyk.pvoptimizer.dto.ResultDto.Builder newBuilder() {
    return new com.github.kacperpotapczyk.pvoptimizer.dto.ResultDto.Builder();
  }

  /**
   * Creates a new ResultDto RecordBuilder by copying an existing Builder.
   * @param other The existing builder to copy.
   * @return A new ResultDto RecordBuilder
   */
  public static com.github.kacperpotapczyk.pvoptimizer.dto.ResultDto.Builder newBuilder(com.github.kacperpotapczyk.pvoptimizer.dto.ResultDto.Builder other) {
    if (other == null) {
      return new com.github.kacperpotapczyk.pvoptimizer.dto.ResultDto.Builder();
    } else {
      return new com.github.kacperpotapczyk.pvoptimizer.dto.ResultDto.Builder(other);
    }
  }

  /**
   * Creates a new ResultDto RecordBuilder by copying an existing ResultDto instance.
   * @param other The existing instance to copy.
   * @return A new ResultDto RecordBuilder
   */
  public static com.github.kacperpotapczyk.pvoptimizer.dto.ResultDto.Builder newBuilder(com.github.kacperpotapczyk.pvoptimizer.dto.ResultDto other) {
    if (other == null) {
      return new com.github.kacperpotapczyk.pvoptimizer.dto.ResultDto.Builder();
    } else {
      return new com.github.kacperpotapczyk.pvoptimizer.dto.ResultDto.Builder(other);
    }
  }

  /**
   * RecordBuilder for ResultDto instances.
   */
  @org.apache.avro.specific.AvroGenerated
  public static class Builder extends org.apache.avro.specific.SpecificRecordBuilderBase<ResultDto>
    implements org.apache.avro.data.RecordBuilder<ResultDto> {

    /** Task identifier */
    private long id;
    private com.github.kacperpotapczyk.pvoptimizer.dto.OptimizationStatusDto optimizationStatus;
    /** Objective function optimal value */
    private double objectiveFunctionValue;
    /** Relative gap between relaxed solution and returned integer solution */
    private double relativeGap;
    /** Optimization elapsed time */
    private double elapsedTime;
    /** Error messages returned by optimizer */
    private java.lang.CharSequence errorMessage;
    private java.util.List<java.lang.Object> contractResults;
    private java.util.List<java.lang.Object> storageResults;
    private java.util.List<java.lang.Object> movableDemandResults;

    /** Creates a new Builder */
    private Builder() {
      super(SCHEMA$, MODEL$);
    }

    /**
     * Creates a Builder by copying an existing Builder.
     * @param other The existing Builder to copy.
     */
    private Builder(com.github.kacperpotapczyk.pvoptimizer.dto.ResultDto.Builder other) {
      super(other);
      if (isValidValue(fields()[0], other.id)) {
        this.id = data().deepCopy(fields()[0].schema(), other.id);
        fieldSetFlags()[0] = other.fieldSetFlags()[0];
      }
      if (isValidValue(fields()[1], other.optimizationStatus)) {
        this.optimizationStatus = data().deepCopy(fields()[1].schema(), other.optimizationStatus);
        fieldSetFlags()[1] = other.fieldSetFlags()[1];
      }
      if (isValidValue(fields()[2], other.objectiveFunctionValue)) {
        this.objectiveFunctionValue = data().deepCopy(fields()[2].schema(), other.objectiveFunctionValue);
        fieldSetFlags()[2] = other.fieldSetFlags()[2];
      }
      if (isValidValue(fields()[3], other.relativeGap)) {
        this.relativeGap = data().deepCopy(fields()[3].schema(), other.relativeGap);
        fieldSetFlags()[3] = other.fieldSetFlags()[3];
      }
      if (isValidValue(fields()[4], other.elapsedTime)) {
        this.elapsedTime = data().deepCopy(fields()[4].schema(), other.elapsedTime);
        fieldSetFlags()[4] = other.fieldSetFlags()[4];
      }
      if (isValidValue(fields()[5], other.errorMessage)) {
        this.errorMessage = data().deepCopy(fields()[5].schema(), other.errorMessage);
        fieldSetFlags()[5] = other.fieldSetFlags()[5];
      }
      if (isValidValue(fields()[6], other.contractResults)) {
        this.contractResults = data().deepCopy(fields()[6].schema(), other.contractResults);
        fieldSetFlags()[6] = other.fieldSetFlags()[6];
      }
      if (isValidValue(fields()[7], other.storageResults)) {
        this.storageResults = data().deepCopy(fields()[7].schema(), other.storageResults);
        fieldSetFlags()[7] = other.fieldSetFlags()[7];
      }
      if (isValidValue(fields()[8], other.movableDemandResults)) {
        this.movableDemandResults = data().deepCopy(fields()[8].schema(), other.movableDemandResults);
        fieldSetFlags()[8] = other.fieldSetFlags()[8];
      }
    }

    /**
     * Creates a Builder by copying an existing ResultDto instance
     * @param other The existing instance to copy.
     */
    private Builder(com.github.kacperpotapczyk.pvoptimizer.dto.ResultDto other) {
      super(SCHEMA$, MODEL$);
      if (isValidValue(fields()[0], other.id)) {
        this.id = data().deepCopy(fields()[0].schema(), other.id);
        fieldSetFlags()[0] = true;
      }
      if (isValidValue(fields()[1], other.optimizationStatus)) {
        this.optimizationStatus = data().deepCopy(fields()[1].schema(), other.optimizationStatus);
        fieldSetFlags()[1] = true;
      }
      if (isValidValue(fields()[2], other.objectiveFunctionValue)) {
        this.objectiveFunctionValue = data().deepCopy(fields()[2].schema(), other.objectiveFunctionValue);
        fieldSetFlags()[2] = true;
      }
      if (isValidValue(fields()[3], other.relativeGap)) {
        this.relativeGap = data().deepCopy(fields()[3].schema(), other.relativeGap);
        fieldSetFlags()[3] = true;
      }
      if (isValidValue(fields()[4], other.elapsedTime)) {
        this.elapsedTime = data().deepCopy(fields()[4].schema(), other.elapsedTime);
        fieldSetFlags()[4] = true;
      }
      if (isValidValue(fields()[5], other.errorMessage)) {
        this.errorMessage = data().deepCopy(fields()[5].schema(), other.errorMessage);
        fieldSetFlags()[5] = true;
      }
      if (isValidValue(fields()[6], other.contractResults)) {
        this.contractResults = data().deepCopy(fields()[6].schema(), other.contractResults);
        fieldSetFlags()[6] = true;
      }
      if (isValidValue(fields()[7], other.storageResults)) {
        this.storageResults = data().deepCopy(fields()[7].schema(), other.storageResults);
        fieldSetFlags()[7] = true;
      }
      if (isValidValue(fields()[8], other.movableDemandResults)) {
        this.movableDemandResults = data().deepCopy(fields()[8].schema(), other.movableDemandResults);
        fieldSetFlags()[8] = true;
      }
    }

    /**
      * Gets the value of the 'id' field.
      * Task identifier
      * @return The value.
      */
    public long getId() {
      return id;
    }


    /**
      * Sets the value of the 'id' field.
      * Task identifier
      * @param value The value of 'id'.
      * @return This builder.
      */
    public com.github.kacperpotapczyk.pvoptimizer.dto.ResultDto.Builder setId(long value) {
      validate(fields()[0], value);
      this.id = value;
      fieldSetFlags()[0] = true;
      return this;
    }

    /**
      * Checks whether the 'id' field has been set.
      * Task identifier
      * @return True if the 'id' field has been set, false otherwise.
      */
    public boolean hasId() {
      return fieldSetFlags()[0];
    }


    /**
      * Clears the value of the 'id' field.
      * Task identifier
      * @return This builder.
      */
    public com.github.kacperpotapczyk.pvoptimizer.dto.ResultDto.Builder clearId() {
      fieldSetFlags()[0] = false;
      return this;
    }

    /**
      * Gets the value of the 'optimizationStatus' field.
      * @return The value.
      */
    public com.github.kacperpotapczyk.pvoptimizer.dto.OptimizationStatusDto getOptimizationStatus() {
      return optimizationStatus;
    }


    /**
      * Sets the value of the 'optimizationStatus' field.
      * @param value The value of 'optimizationStatus'.
      * @return This builder.
      */
    public com.github.kacperpotapczyk.pvoptimizer.dto.ResultDto.Builder setOptimizationStatus(com.github.kacperpotapczyk.pvoptimizer.dto.OptimizationStatusDto value) {
      validate(fields()[1], value);
      this.optimizationStatus = value;
      fieldSetFlags()[1] = true;
      return this;
    }

    /**
      * Checks whether the 'optimizationStatus' field has been set.
      * @return True if the 'optimizationStatus' field has been set, false otherwise.
      */
    public boolean hasOptimizationStatus() {
      return fieldSetFlags()[1];
    }


    /**
      * Clears the value of the 'optimizationStatus' field.
      * @return This builder.
      */
    public com.github.kacperpotapczyk.pvoptimizer.dto.ResultDto.Builder clearOptimizationStatus() {
      optimizationStatus = null;
      fieldSetFlags()[1] = false;
      return this;
    }

    /**
      * Gets the value of the 'objectiveFunctionValue' field.
      * Objective function optimal value
      * @return The value.
      */
    public double getObjectiveFunctionValue() {
      return objectiveFunctionValue;
    }


    /**
      * Sets the value of the 'objectiveFunctionValue' field.
      * Objective function optimal value
      * @param value The value of 'objectiveFunctionValue'.
      * @return This builder.
      */
    public com.github.kacperpotapczyk.pvoptimizer.dto.ResultDto.Builder setObjectiveFunctionValue(double value) {
      validate(fields()[2], value);
      this.objectiveFunctionValue = value;
      fieldSetFlags()[2] = true;
      return this;
    }

    /**
      * Checks whether the 'objectiveFunctionValue' field has been set.
      * Objective function optimal value
      * @return True if the 'objectiveFunctionValue' field has been set, false otherwise.
      */
    public boolean hasObjectiveFunctionValue() {
      return fieldSetFlags()[2];
    }


    /**
      * Clears the value of the 'objectiveFunctionValue' field.
      * Objective function optimal value
      * @return This builder.
      */
    public com.github.kacperpotapczyk.pvoptimizer.dto.ResultDto.Builder clearObjectiveFunctionValue() {
      fieldSetFlags()[2] = false;
      return this;
    }

    /**
      * Gets the value of the 'relativeGap' field.
      * Relative gap between relaxed solution and returned integer solution
      * @return The value.
      */
    public double getRelativeGap() {
      return relativeGap;
    }


    /**
      * Sets the value of the 'relativeGap' field.
      * Relative gap between relaxed solution and returned integer solution
      * @param value The value of 'relativeGap'.
      * @return This builder.
      */
    public com.github.kacperpotapczyk.pvoptimizer.dto.ResultDto.Builder setRelativeGap(double value) {
      validate(fields()[3], value);
      this.relativeGap = value;
      fieldSetFlags()[3] = true;
      return this;
    }

    /**
      * Checks whether the 'relativeGap' field has been set.
      * Relative gap between relaxed solution and returned integer solution
      * @return True if the 'relativeGap' field has been set, false otherwise.
      */
    public boolean hasRelativeGap() {
      return fieldSetFlags()[3];
    }


    /**
      * Clears the value of the 'relativeGap' field.
      * Relative gap between relaxed solution and returned integer solution
      * @return This builder.
      */
    public com.github.kacperpotapczyk.pvoptimizer.dto.ResultDto.Builder clearRelativeGap() {
      fieldSetFlags()[3] = false;
      return this;
    }

    /**
      * Gets the value of the 'elapsedTime' field.
      * Optimization elapsed time
      * @return The value.
      */
    public double getElapsedTime() {
      return elapsedTime;
    }


    /**
      * Sets the value of the 'elapsedTime' field.
      * Optimization elapsed time
      * @param value The value of 'elapsedTime'.
      * @return This builder.
      */
    public com.github.kacperpotapczyk.pvoptimizer.dto.ResultDto.Builder setElapsedTime(double value) {
      validate(fields()[4], value);
      this.elapsedTime = value;
      fieldSetFlags()[4] = true;
      return this;
    }

    /**
      * Checks whether the 'elapsedTime' field has been set.
      * Optimization elapsed time
      * @return True if the 'elapsedTime' field has been set, false otherwise.
      */
    public boolean hasElapsedTime() {
      return fieldSetFlags()[4];
    }


    /**
      * Clears the value of the 'elapsedTime' field.
      * Optimization elapsed time
      * @return This builder.
      */
    public com.github.kacperpotapczyk.pvoptimizer.dto.ResultDto.Builder clearElapsedTime() {
      fieldSetFlags()[4] = false;
      return this;
    }

    /**
      * Gets the value of the 'errorMessage' field.
      * Error messages returned by optimizer
      * @return The value.
      */
    public java.lang.CharSequence getErrorMessage() {
      return errorMessage;
    }


    /**
      * Sets the value of the 'errorMessage' field.
      * Error messages returned by optimizer
      * @param value The value of 'errorMessage'.
      * @return This builder.
      */
    public com.github.kacperpotapczyk.pvoptimizer.dto.ResultDto.Builder setErrorMessage(java.lang.CharSequence value) {
      validate(fields()[5], value);
      this.errorMessage = value;
      fieldSetFlags()[5] = true;
      return this;
    }

    /**
      * Checks whether the 'errorMessage' field has been set.
      * Error messages returned by optimizer
      * @return True if the 'errorMessage' field has been set, false otherwise.
      */
    public boolean hasErrorMessage() {
      return fieldSetFlags()[5];
    }


    /**
      * Clears the value of the 'errorMessage' field.
      * Error messages returned by optimizer
      * @return This builder.
      */
    public com.github.kacperpotapczyk.pvoptimizer.dto.ResultDto.Builder clearErrorMessage() {
      errorMessage = null;
      fieldSetFlags()[5] = false;
      return this;
    }

    /**
      * Gets the value of the 'contractResults' field.
      * @return The value.
      */
    public java.util.List<java.lang.Object> getContractResults() {
      return contractResults;
    }


    /**
      * Sets the value of the 'contractResults' field.
      * @param value The value of 'contractResults'.
      * @return This builder.
      */
    public com.github.kacperpotapczyk.pvoptimizer.dto.ResultDto.Builder setContractResults(java.util.List<java.lang.Object> value) {
      validate(fields()[6], value);
      this.contractResults = value;
      fieldSetFlags()[6] = true;
      return this;
    }

    /**
      * Checks whether the 'contractResults' field has been set.
      * @return True if the 'contractResults' field has been set, false otherwise.
      */
    public boolean hasContractResults() {
      return fieldSetFlags()[6];
    }


    /**
      * Clears the value of the 'contractResults' field.
      * @return This builder.
      */
    public com.github.kacperpotapczyk.pvoptimizer.dto.ResultDto.Builder clearContractResults() {
      contractResults = null;
      fieldSetFlags()[6] = false;
      return this;
    }

    /**
      * Gets the value of the 'storageResults' field.
      * @return The value.
      */
    public java.util.List<java.lang.Object> getStorageResults() {
      return storageResults;
    }


    /**
      * Sets the value of the 'storageResults' field.
      * @param value The value of 'storageResults'.
      * @return This builder.
      */
    public com.github.kacperpotapczyk.pvoptimizer.dto.ResultDto.Builder setStorageResults(java.util.List<java.lang.Object> value) {
      validate(fields()[7], value);
      this.storageResults = value;
      fieldSetFlags()[7] = true;
      return this;
    }

    /**
      * Checks whether the 'storageResults' field has been set.
      * @return True if the 'storageResults' field has been set, false otherwise.
      */
    public boolean hasStorageResults() {
      return fieldSetFlags()[7];
    }


    /**
      * Clears the value of the 'storageResults' field.
      * @return This builder.
      */
    public com.github.kacperpotapczyk.pvoptimizer.dto.ResultDto.Builder clearStorageResults() {
      storageResults = null;
      fieldSetFlags()[7] = false;
      return this;
    }

    /**
      * Gets the value of the 'movableDemandResults' field.
      * @return The value.
      */
    public java.util.List<java.lang.Object> getMovableDemandResults() {
      return movableDemandResults;
    }


    /**
      * Sets the value of the 'movableDemandResults' field.
      * @param value The value of 'movableDemandResults'.
      * @return This builder.
      */
    public com.github.kacperpotapczyk.pvoptimizer.dto.ResultDto.Builder setMovableDemandResults(java.util.List<java.lang.Object> value) {
      validate(fields()[8], value);
      this.movableDemandResults = value;
      fieldSetFlags()[8] = true;
      return this;
    }

    /**
      * Checks whether the 'movableDemandResults' field has been set.
      * @return True if the 'movableDemandResults' field has been set, false otherwise.
      */
    public boolean hasMovableDemandResults() {
      return fieldSetFlags()[8];
    }


    /**
      * Clears the value of the 'movableDemandResults' field.
      * @return This builder.
      */
    public com.github.kacperpotapczyk.pvoptimizer.dto.ResultDto.Builder clearMovableDemandResults() {
      movableDemandResults = null;
      fieldSetFlags()[8] = false;
      return this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public ResultDto build() {
      try {
        ResultDto record = new ResultDto();
        record.id = fieldSetFlags()[0] ? this.id : (java.lang.Long) defaultValue(fields()[0]);
        record.optimizationStatus = fieldSetFlags()[1] ? this.optimizationStatus : (com.github.kacperpotapczyk.pvoptimizer.dto.OptimizationStatusDto) defaultValue(fields()[1]);
        record.objectiveFunctionValue = fieldSetFlags()[2] ? this.objectiveFunctionValue : (java.lang.Double) defaultValue(fields()[2]);
        record.relativeGap = fieldSetFlags()[3] ? this.relativeGap : (java.lang.Double) defaultValue(fields()[3]);
        record.elapsedTime = fieldSetFlags()[4] ? this.elapsedTime : (java.lang.Double) defaultValue(fields()[4]);
        record.errorMessage = fieldSetFlags()[5] ? this.errorMessage : (java.lang.CharSequence) defaultValue(fields()[5]);
        record.contractResults = fieldSetFlags()[6] ? this.contractResults : (java.util.List<java.lang.Object>) defaultValue(fields()[6]);
        record.storageResults = fieldSetFlags()[7] ? this.storageResults : (java.util.List<java.lang.Object>) defaultValue(fields()[7]);
        record.movableDemandResults = fieldSetFlags()[8] ? this.movableDemandResults : (java.util.List<java.lang.Object>) defaultValue(fields()[8]);
        return record;
      } catch (org.apache.avro.AvroMissingFieldException e) {
        throw e;
      } catch (java.lang.Exception e) {
        throw new org.apache.avro.AvroRuntimeException(e);
      }
    }
  }

  @SuppressWarnings("unchecked")
  private static final org.apache.avro.io.DatumWriter<ResultDto>
    WRITER$ = (org.apache.avro.io.DatumWriter<ResultDto>)MODEL$.createDatumWriter(SCHEMA$);

  @Override public void writeExternal(java.io.ObjectOutput out)
    throws java.io.IOException {
    WRITER$.write(this, SpecificData.getEncoder(out));
  }

  @SuppressWarnings("unchecked")
  private static final org.apache.avro.io.DatumReader<ResultDto>
    READER$ = (org.apache.avro.io.DatumReader<ResultDto>)MODEL$.createDatumReader(SCHEMA$);

  @Override public void readExternal(java.io.ObjectInput in)
    throws java.io.IOException {
    READER$.read(this, SpecificData.getDecoder(in));
  }

}









