package org.egov.im.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.sql.DataSource;

import org.egov.im.config.PropertiesManager;
import org.egov.im.web.contract.factory.ResponseInfoFactory;
import org.egov.im.web.models.RequestInfo;
import org.egov.im.web.models.Idgen.IdGenerationRequest;
import org.egov.im.web.models.Idgen.IdGenerationResponse;
import org.egov.im.web.models.Idgen.IdRequest;
import org.egov.im.web.models.Idgen.IdResponse;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import lombok.extern.slf4j.Slf4j;


/**
 * Description : IdGenerationService have methods related to the IdGeneration
 *
 * @author Pavan Kumar Kamma
 */
@Service
@Slf4j
public class IdGenerationService {

    @Autowired
    DataSource dataSource;

    @Autowired
    PropertiesManager propertiesManager;

    @Autowired
    private ResponseInfoFactory responseInfoFactory;

    @Autowired
    private JdbcTemplate jdbcTemplate;


    //By default the auto create sequence is disabled
    @Value("${autocreate.new.seq}")
    public boolean autoCreateNewSeq;


    //default count value
    public Integer defaultCount = 1;


    /**
     * Description : This method to generate idGenerationResponse
     *
     * @param idGenerationRequest
     * @return idGenerationResponse
     * @throws Exception
     */

    public IdGenerationResponse generateIdResponse(IdGenerationRequest idGenerationRequest) throws Exception {

        RequestInfo requestInfo = idGenerationRequest.getRequestInfo();
        List<IdRequest> idRequests = idGenerationRequest.getIdRequests();
        List<IdResponse> idResponses = new LinkedList<>();

        IdGenerationResponse idGenerationResponse = new IdGenerationResponse();

        for (IdRequest idRequest : idRequests) {
            List<String> generatedId = generateIdFromIdRequest(idRequest, requestInfo);
            for (String ListOfIds : generatedId) {
                IdResponse idResponse = new IdResponse();
                idResponse.setId(ListOfIds);
                idResponses.add(idResponse);
            }
            idGenerationResponse.setIdResponses(idResponses);
        }
        idGenerationResponse.setResponseInfo(responseInfoFactory.createResponseInfoFromRequestInfo(requestInfo, true));

        return idGenerationResponse;

    }

    ;

    /**
     * Description : This method to generate id
     *
     * @param idRequest
     * @param requestInfo
     * @return generatedId
     * @throws Exception
     */
    private List generateIdFromIdRequest(IdRequest idRequest, RequestInfo requestInfo) throws Exception {

        List<String> generatedId = new LinkedList<>();
        boolean autoCreateNewSeqFlag = false;
        if (!StringUtils.isEmpty(idRequest.getIdName()))
        {
            String idFormat =idRequest.getFormat();
            if (!StringUtils.isEmpty(idFormat)){
                idRequest.setFormat(idFormat);
                autoCreateNewSeqFlag=true;
            }else if(StringUtils.isEmpty(idFormat)){
                autoCreateNewSeqFlag=false;
            }
        }

        if (StringUtils.isEmpty(idRequest.getFormat()))
            throw new CustomException("ID_NOT_FOUND",
                    "No Format is available in the MDMS for the given name and tenant");

        return getFormattedId(idRequest, requestInfo,autoCreateNewSeqFlag);
    }


        /**
     * Description : This method to generate Id when format is known
     *
     * @param idRequest
     * @param requestInfo
     * @return formattedId
     * @throws Exception
     */

    private List getFormattedId(IdRequest idRequest, RequestInfo requestInfo, boolean autoCreateNewSeqFlag) throws Exception {
        List<String> idFormatList = new LinkedList();
        String idFormat = idRequest.getFormat();

        List<String> matchList = new ArrayList<String>();

        Pattern regExpPattern = Pattern.compile("\\[(.*?)\\]");
        Matcher regExpMatcher = regExpPattern.matcher(idFormat);

        Integer count = getCount(idRequest);

        while (regExpMatcher.find()) {// Finds Matching Pattern in String
            matchList.add(regExpMatcher.group(1));// Fetching Group from String
        }

        HashMap<String, List<String>> sequences = new HashMap<>();
        String idFormatTemplate = idFormat;
        String cityName = null;

        for (int i = 0; i < count; i++) {
            idFormat = idFormatTemplate;

            for (String attributeName : matchList) {

                if (attributeName.substring(0, 3).equalsIgnoreCase("seq")) {
                    if (!sequences.containsKey(attributeName)) {
                        sequences.put(attributeName, generateSequenceNumber(attributeName, requestInfo, idRequest,autoCreateNewSeqFlag));
                    }
					idFormat = idFormat.replace("[" + attributeName + "]", sequences.get(attributeName).get(i));
                } else if (attributeName.substring(0, 2).equalsIgnoreCase("fy")) {
                    idFormat = idFormat.replace("[" + attributeName + "]",
                            generateFinancialYearDateFormat(attributeName, requestInfo));
                } else if (attributeName.substring(0, 2).equalsIgnoreCase("cy")) {
                    idFormat = idFormat.replace("[" + attributeName + "]",
                            generateCurrentYearDateFormat(attributeName, requestInfo));
                }  else {
                    idFormat = idFormat.replace("[" + attributeName + "]", generateRandomText(attributeName, requestInfo));
                }
            }
            idFormatList.add(idFormat);
        }

        return idFormatList;
    }

    /**
     * Description : This method to generate current financial year in given
     * format
     *
     * @param requestInfo
     * @return formattedDate
     */
    private String generateFinancialYearDateFormat(String financialYearFormat, RequestInfo requestInfo) {
        try {

            Date date = new Date();
            financialYearFormat = financialYearFormat.substring(financialYearFormat.indexOf(":") + 1);
            financialYearFormat = financialYearFormat.trim();
            String currentFinancialYear = null;
            String[] financialYearPatternArray;
            financialYearPatternArray = financialYearFormat.split("-");
            int month = Calendar.getInstance().get(Calendar.MONTH) + 1;
            int preYear = 0;
            int postYear = 0;

            for (String yearPattern : financialYearPatternArray) {

                String formattedYear = null;
                SimpleDateFormat formatter = new SimpleDateFormat(yearPattern.trim());
                formattedYear = formatter.format(date);

                if (financialYearPatternArray[0] == yearPattern) {
                    if (month > 3) {
                        preYear = Integer.valueOf(formattedYear);
                    } else {
                        preYear = Integer.valueOf(formattedYear) - 1;
                    }
                } else {
                    if (month > 3) {
                        postYear = Integer.valueOf(formattedYear) + 1;
                    } else {
                        postYear = Integer.valueOf(formattedYear);
                    }
                }
            }
            currentFinancialYear = preYear + "-" + postYear;
            return currentFinancialYear;

        } catch (Exception e) {

            throw new CustomException("INVALID_FORMAT", "Error while generating financial year in provided format. Given format invalid.");
            //throw new InvalidIDFormatException(propertiesManager.getInvalidIdFormat(), requestInfo);

        }
    }

    /**
     * Description : This method to generate current year date in given format
     *
     * @param dateFormat
     * @param requestInfo
     * @return formattedDate
     */
    private String generateCurrentYearDateFormat(String dateFormat, RequestInfo requestInfo) {
        try {

            Date date = new Date();
            dateFormat = dateFormat.trim();
            dateFormat = dateFormat.substring(dateFormat.indexOf(":") + 1);
            dateFormat = dateFormat.trim();
            SimpleDateFormat formatter = new SimpleDateFormat(dateFormat.trim());
            formatter.setTimeZone(TimeZone.getTimeZone(propertiesManager.getTimeZone()));
            String formattedDate = formatter.format(date);
            return formattedDate;

        } catch (Exception e) {

            throw new CustomException("INVALID_FORMAT", "Error while generating current year in provided format. Given format invalid.");
            //throw new InvalidIDFormatException(propertiesManager.getInvalidIdFormat(), requestInfo);

        }
    }

    /**
     * Description : This method to generate random text
     *
     * @param regex
     * @param requestInfo
     * @return randomTxt
     */
    private String generateRandomText(String regex, RequestInfo requestInfo) {
        Random random = new Random();
        List<String> matchList = new ArrayList<String>();
        int length = 2;// default digits length
        try {
            Pattern.compile(regex);
        } catch (Exception e) {
            throw new CustomException("INVALID_REGEX", "Random text could not be generated. Invalid regex provided.");
            //throw new InvalidIDFormatException(propertiesManager.getInvalidIdFormat(), requestInfo);
        }
        Matcher matcher = Pattern.compile("\\{(.*?)\\}").matcher(regex);
        while (matcher.find()) {
            matchList.add(matcher.group(1));
        }
        if (matchList.size() > 0) {
            length = Integer.parseInt(matchList.get(0));
        }
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < length; i++) {
            stringBuilder.append(random.nextInt(25));
        }
        String randomTxt = stringBuilder.toString();
        randomTxt = randomTxt.substring(0, length);
        return randomTxt;
    }

    /**
     * Description : This method to set default count value
     *
     * @param idRequest
     * @return count
     */
    private Integer getCount(IdRequest idRequest) {
        Integer count;
        if (idRequest.getCount() == null) {
            count = defaultCount;
        } else {
            count = idRequest.getCount();
        }
        return count;
    }

    /**
     * Description : This method to generate sequence in DB
     *
     * @param sequenceName
     */

    private void createSequenceInDb(String sequenceName) throws Exception {

        StringBuilder query = new StringBuilder("CREATE SEQUENCE ");
        try {
            query = query.append(sequenceName);
            jdbcTemplate.execute(query.toString());
        }catch (Exception ex){
            log.error("Error creating new sequence",ex);
        }
    }

    /**
     * Description : This method to generate sequence number
     *
     * @param sequenceName
     * @param requestInfo
     * @return seqNumber
     */
    private List<String> generateSequenceNumber(String sequenceName, RequestInfo requestInfo, IdRequest idRequest,boolean autoCreateNewSeqFlag) throws Exception {
        Integer count = getCount(idRequest);
        List<String> sequenceList = new LinkedList<>();
        List<String> sequenceLists = new LinkedList<>();
        // To generate a block of seq numbers

        String sequenceSql = "SELECT NEXTVAL ('" + sequenceName + "') FROM GENERATE_SERIES(1,?)";
        try {
            sequenceList = jdbcTemplate.queryForList(sequenceSql, new Object[]{count}, String.class);
        } catch (BadSqlGrammarException ex) {
            if (ex.getSQLException().getSQLState().equals("42P01")){
                try{
                    if (sequenceList.isEmpty() && autoCreateNewSeqFlag && autoCreateNewSeq){
                        createSequenceInDb(sequenceName);
                        sequenceList = jdbcTemplate.queryForList(sequenceSql, new Object[]{count}, String.class);
                    }
                    else if(sequenceList.isEmpty() && !autoCreateNewSeqFlag)
                        throw new CustomException("SEQ_DOES_NOT_EXIST","auto creation of seq is not allowed in DB");
                }catch(Exception e) {
                    throw new CustomException("ERROR_CREATING_SEQ","Error occurred while auto creating seq in DB");
                }
            }else{
                throw new CustomException("SEQ_NUMBER_ERROR","Error in retrieving seq number from DB");
            }
        } catch (Exception ex) {
            log.error("Error retrieving seq number from DB",ex);
            throw new CustomException("SEQ_NUMBER_ERROR","Error retrieving seq number from existing seq in DB");
        }
        for (String seqId : sequenceList) {
            String seqNumber = String.format("%06d", Integer.parseInt(seqId)).toString();
            sequenceLists.add(seqNumber.toString());
        }
        return sequenceLists;
    }

}
