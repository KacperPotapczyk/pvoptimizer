/**
 * Autogenerated by Avro
 *
 * DO NOT EDIT DIRECTLY
 */
package com.github.kacperpotapczyk.pvoptimizer.dto;
@org.apache.avro.specific.AvroGenerated
public enum StorageModeDto implements org.apache.avro.generic.GenericEnumSymbol<StorageModeDto> {
  DISABLED, CHARGING, DISCHARGING  ;
  public static final org.apache.avro.Schema SCHEMA$ = new org.apache.avro.Schema.Parser().parse("{\"type\":\"enum\",\"name\":\"StorageModeDto\",\"namespace\":\"com.github.kacperpotapczyk.pvoptimizer.dto\",\"symbols\":[\"DISABLED\",\"CHARGING\",\"DISCHARGING\"]}");
  public static org.apache.avro.Schema getClassSchema() { return SCHEMA$; }

  @Override
  public org.apache.avro.Schema getSchema() { return SCHEMA$; }
}
