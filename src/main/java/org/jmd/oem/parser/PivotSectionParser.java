package org.jmd.oem.parser;

import com.google.gson.Gson;
import org.jmd.oem.util.PoiUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

//TODO Create gradle for library
//TODO add pluggins for pmd checkstyle and spotbug.
//TODO create a github project and create the gitactions build
// Deploy the artifact in maven central
public class PivotSectionParser<T> extends SectionParser<List<T>> {
    private static final Logger LOG = LoggerFactory.getLogger(PivotSectionParser.class);

    protected PivotSectionParser(String name, List<T> objectToBuild, SectionParserContext<T> context) {
        super(name, objectToBuild, context);
    }

    /**
     * In this case we are assuming that the value in the cell in columnStartIndex is the column name
     * and the next cell is the value so cell[columnStartIndex+1]
     * In this case the objectToBuild is being populated in the field tha has the column name=cell[columnStartIndex]
     * with the value=cell[columnStartIndex+1]
     *
     * @param row
     */
    //TODO try to change the un set to null using Integer instead of -1
    @Override
    public void doAccept(Row row) {
        try {
            String columnName = PoiUtil.cellStringTrim(row.getCell(context.getColumnStartIndex()));
            if (StringUtil.isNotBlank(columnName)) {
                int sectionLastCellIndex = getSectionLastCellIndex(row);
                int firstValue = context.getColumnStartIndex() + 1;
                for (int i = firstValue; i < sectionLastCellIndex; i++) {
                    populateRowObject(objectToBuild.get(i - firstValue), columnName, row.getCell(i));
                }
            } else {
                LOG.debug("Empty line found");
            }
        } catch (Exception exception) {
            LOG.debug(new Gson().toJson(objectToBuild));
            LOG.error("Error populating object for row {}", getRowCount(), exception);
        }
    }

    @Override
    public void doFirstRow(Row row) {
        int sectionLastCellIndex = getSectionLastCellIndex(row);
        for (int i = context.getColumnStartIndex() + 1; i < sectionLastCellIndex; i++) {
            objectToBuild.add((T) newInstance());
        }
        doAccept(row);
    }

    @Override
    protected void printResume() {
        LOG.info("HEADERS {}", new Gson().toJson(get()));
    }


}