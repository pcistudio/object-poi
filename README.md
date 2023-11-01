# Object Poi
It is a library built on top of Apache POI with the goal of creating an Object Excel Mapping library designed to simplify the process of parsing intricate Excel documents into objects.

## Getting Started

### Gradle:
```
implementation 'com.pcistudio:object-poi:${latestVersion}'
```

### Maven:
```xml
<dependency>
    <groupId>com.pcistudio</groupId>
    <artifact>object-poi</artifact>
    <version>${latestVersion}</version>
</dependency>
```
## Overview
* In Object Poi we treat every sheet as a group of sections, and the sections as a list of rows. For the sections we have 2 types:
 `table` when the columns are in a row. Example:

   | columnName1 | columnName1 |
   |-------------|-------------|
   | value1      | value2      |
   | value3      | value4      |
* `pivot` when the columns are vertical in a single column. Example:

    | columnName1 | value1 | value4 |
    |-------------|--------|--------|
    | columnName2 | value2 | value5 |
    | columnName3 | value3 | value6 |

Every sheet represent an object that contain one list for each section in that sheet. 
In the following example we have 3 Sections.
1. First section is the header for this report where they will describe some details of this report, this will be described using a pivot.
2. Second has most of the data of this report, and it will be described using a table.
3. For the last section in this example we will use a pivot again to describe it.

![Alt text](doc/revenue_sample.png "Revenue Report")

## SectionParserManagerBuilder
To describe each section we use the SectionParserManagerBuilder 
that will give you the choice to configure a `pivot` using  `PivotSectionParserBuilder` or a `table` using `TableSectionParserBuilder`

### PivotSectionParserBuilder

| Method                     | Description                                                                                                                                                                                                         |
|----------------------------|---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| withName(String)           | Name of the Section                                                                                                                                                                                                 |
| withObjectToBuild(List<T>) | This is the reference the list of record objects extracted form the excel. This list will be populated by the library                                                                                               |
| withStartValue(String)     | Value used to identify where the section start, by default it will look in the first column if `columnStartIndex` is not set, until it find the start value in one of the cells                                     |
| withRowStartIndex(int)     | It will start the section in the set index                                                                                                                                                                          |
| withColumnStartIndex(int)  | useful when setting `withStartValue` if the value to search is not in the first column                                                                                                                              |
| withColumnCount(int)       | This property define the number of columns that the library will read for that section in each row. Remember in this case the records are vertical. ***If not set, I will try to read all the columns in the row*** |  
| withRecordClass(Class<T>)  | This will define the record class                                                                                                                                                                                   |
| keyValue()                 | is a shortcut for `withColumnCount(2)` that expect only one record in the excel                                                                                                                                     |                                                                                              |


### TableSectionParserBuilder

| Method                     | Description                                                                                                                                                                     |
|----------------------------|---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| withName(String)           | Name of the Section                                                                                                                                                             |
| withObjectToBuild(List<T>) | This is the reference the list of record objects extracted form the excel. This list will be populated by the library                                                           |
| withStartValue(String)     | Value used to identify where the section start, by default it will look in the first column if `columnStartIndex` is not set, until it find the start value in one of the cells |
| withRowStartIndex(int)     | It will start the section in the set index                                                                                                                                      |
| withColumnCount(int)       | This property define the number of columns that the library will read for that section in each row.  ***If not set, I will try to read all the columns in the row*** |  
| withColumnStartIndex(int)  | useful when setting `withStartValue` if the value to search is not in the first column                                                                                          |
| withRecordClass(Class<T>)  | This will define the record class                                                                                                                                               |



## Example
First extends `ColumnSheetParser<T>` with the type of object present in the Excel sheet. Complex Excel sheets are compose by sections.
In this case the object `CarRentalRevenue` defined by:
```java
public class CarRentalRevenue {
    private List<CarRentalHeader> carRentalHeaders = new ArrayList<>();
    private List<CarRentalRecord> carRentalRecords = new ArrayList<>();

    public CarRentalRevenue() {
    }

    public void addInventoryRow(CarRentalRecord carRentalRecord) {
        if (carRentalRecord != null) {
            carRentalRecords.add(carRentalRecord);
        }
    }

    public List<CarRentalHeader> getCarRentalHeaders() {
        return carRentalHeaders;
    }

    public List<CarRentalRecord> getCarRentalRecords() {
        return carRentalRecords;
    }
}
```
`CarRentalRevenue` each section is represented by a list of the record in that section. In this case we only put 2 sections because we want to skip the last section.

The record class [CarRentalHeader](src/test/java/com/pcistudio/poi/report/CarRentalHeader.java) and [CarRentalRecord](src/test/java/com/pcistudio/poi/report/CarRentalRecord.java) are annotated with `@DataField` annotation, that map the column name with the object field.

In this example the current sheet is having 3 sections:
1. Pivot table of CarRentalHeader records with `keyValue` to read only column name and value, starting at index 0.
2. Normal table with name MainTable starting at index 9 with the column name row.
3. Last section start when the start value `Total Revenue:` is found, I this case  the section will be ignored because `recordType` was not set. Useful in case that you want to skip the section.

```java
public class RevenueColumnSheetParser extends ColumnSheetParser<CarRentalRevenue> {

    @Override
    protected void describeSections(CarRentalRevenue carRentalRevenue, SectionParserManagerBuilder builder) {
//        Section1
        builder.pivot(CarRentalHeader.class)
                .describe(
                        config -> config
                                .withName("Report Header")
                                .withObjectToBuild(carRentalRevenue.getCarRentalHeaders())
                                .keyValue()
                                .withRowStartIndex(0));
//        Section2
        builder.table(CarRentalRecord.class)
                .describe(
                        config -> config.withName("Revenue by Car")
                                .withObjectToBuild(carRentalRevenue.getCarRentalRecords())
                                .withStartValue("Plate Number"));
//        Section3
        builder.pivot()
                .describe(config -> config.withName("Total")
                        .withColumnStartIndex(6)
                        .keyValue()
                        .withStartValue("Total Revenue\n(Last Month)"));

    }

    @Override
    public String getSheetName() {
        return "Revenue";
    }
}
```
TODO: Missing the explanation for how the library chose the sheetname to read
```java
import com.pcistudio.poi.processor.WorkbookProcessor;
...
public static void main(String[]args){
        WorkbookProcessor workbookProcessor = new WorkbookProcessor(dataSheetParser());
}

```

------------------------

> Note: This version don't support formula at writing time just at reading. 
> Any value that needs come from formulas need to be pre-calculated.


