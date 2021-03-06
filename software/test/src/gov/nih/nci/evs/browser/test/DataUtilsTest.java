/*L
 * Copyright Northrop Grumman Information Technology.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/nci-term-browser/LICENSE.txt for details.
 */

package gov.nih.nci.evs.browser.test;

import org.LexGrid.LexBIG.DataModel.Collections.*;
import org.LexGrid.LexBIG.DataModel.Core.*;
import org.LexGrid.LexBIG.DataModel.InterfaceElements.*;
import org.LexGrid.LexBIG.LexBIGService.*;
import org.apache.log4j.*;

import gov.nih.nci.evs.browser.test.utils.*;
import gov.nih.nci.evs.browser.utils.*;

public class DataUtilsTest {
    private static Logger _logger = getLogger(); 

    private static Logger getLogger() {
        Logger.getLogger("gov.nih.nci.evs").setLevel(Level.DEBUG);
        Class<DataUtilsTest> c = DataUtilsTest.class;
        Logger.getLogger(c).setLevel(Level.DEBUG);
        return Logger.getLogger(c);
    }
    
    private static void debug(String text) {
        //System.out.println(text);
        _logger.debug(text);
    }
    
    public static void getVocabularyVersionByTagTest() throws Exception {
        LexBIGService lbSvc = RemoteServerUtil.createLexBIGService();
        CodingSchemeRenderingList csrl = lbSvc.getSupportedCodingSchemes();
        CodingSchemeRendering[] csrs = csrl.getCodingSchemeRendering();
        for (int i = 0; i < csrs.length; i++) {
            CodingSchemeRendering csr = csrs[i];
            CodingSchemeSummary css = csr.getCodingSchemeSummary();
            String formalName = css.getFormalName();
            String version = css.getRepresentsVersion();
            String prodVersion =
                DataUtils.getVocabularyVersionByTag(formalName, "PRODUCTION");
            debug(formalName);
            debug("  * version:    " + version);
            debug("  * production: " + prodVersion);
        }
    }

    public static void main(String[] args) throws Exception {
        args = SetupEnv.getInstance().parse(args);
        DataUtilsTest.getVocabularyVersionByTagTest();
    }
}
