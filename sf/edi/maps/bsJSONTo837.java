// header tag info
String timestamp = now(); // yyyyMMddhhmm

mapSegment("BHT","e01","0019");
mapSegment("BHT","e02","00");
mapSegment("BHT","e03",getInput("claimHeader","interchangeControlNumber"));
mapSegment("BHT","e04",timestamp.substring(0,8));
mapSegment("BHT","e05",timestamp.substring(8,12));
mapSegment("BHT","e06","CH");
commitSegment("BHT");

//int condcount = getLoopCount("claimHeader:claims:claimInformation:healthCareCodeInformations", 3);
//mapSegment("BHT","e04",snum(condcount));


// submitter
mapSegment("NM1","e01","41");
mapSegment("NM1","e02","2");
mapSegment("NM1","e03",getInput("claimHeader:claims:submitter","lastNameOrOrganizationName"));
mapSegment("NM1","e08","46");
mapSegment("NM1","e09",getInput("claimHeader:claims:submitter","electronicTransmissionIdentifierNumber"));
commitSegment("NM1");
mapSegment("PER","e01","IC");
mapSegment("PER","e02",getInput("claimHeader:claims:submitter:contactInformations","contactName"));
mapSegment("PER","e03","TE");
mapSegment("PER","e04",getInput("claimHeader:claims:submitter:contactInformations","phoneNumber"));
commitSegment("PER");

// receiver
mapSegment("NM1","e01","40");
mapSegment("NM1","e02","2");
mapSegment("NM1","e03",getInput("claimHeader:claims:receiver","organizationName"));
mapSegment("NM1","e08","46");
mapSegment("NM1","e09",getInput("claimHeader:claims:receiver","receiverId"));
commitSegment("NM1");

int HLCounter = 1;

// Billing Provider
mapSegment("HL","e01",snum(HLCounter));
mapSegment("HL","e03","20");
mapSegment("HL","e04","1");
commitSegment("HL");
mapSegment("NM1","e01","85");
mapSegment("NM1","e02","2");
mapSegment("NM1","e03",getInput("claimHeader:claims:billing","lastNameOrOrganizationName"));
mapSegment("NM1","e04","11");
mapSegment("NM1","e08","XX");
mapSegment("NM1","e09",getInput("claimHeader:claims:billing","taxonomyCode"));
commitSegment("NM1");
mapSegment("N3","e01",getInput("claimHeader:claims:billing","address1"));
commitSegment("N3");
mapSegment("N4","e01",getInput("claimHeader:claims:billing","city"));
mapSegment("N4","e02",getInput("claimHeader:claims:billing","state"));
mapSegment("N4","e03",getInput("claimHeader:claims:billing","postalCode"));
commitSegment("N4");

// subscriber
HLCounter++;
mapSegment("HL","e01",snum(HLCounter));
mapSegment("HL","e02",snum(HLCounter - 1));
mapSegment("HL","e03","22");
mapSegment("HL","e04","1");
commitSegment("HL");
mapSegment("SBR","e01",getInput("claimHeader:claims:subscriber","paymentResponsibilityLevelCode"));
mapSegment("SBR","e03",getInput("claimHeader:claims:subscriber","groupOrPolicyNumber"));
mapSegment("SBR","e09","12");
commitSegment("SBR");
mapSegment("NM1","e01","IL");
mapSegment("NM1","e02","1");
mapSegment("NM1","e03",getInput("claimHeader:claims:subscriber","lastNameOrOrganizationName"));
mapSegment("NM1","e04","11");
mapSegment("NM1","e08","XX");
mapSegment("NM1","e09",getInput("claimHeader:claims:subscriber","memberId"));
commitSegment("NM1");
mapSegment("N3","e01",getInput("claimHeader:claims:subscriber","address1"));
commitSegment("N3");
mapSegment("N4","e01",getInput("claimHeader:claims:subscriber","city"));
mapSegment("N4","e02",getInput("claimHeader:claims:subscriber","state"));
mapSegment("N4","e03",getInput("claimHeader:claims:subscriber","postalCode"));
commitSegment("N4");
mapSegment("DMG","e01","D8");
mapSegment("DMG","e02",getInput("claimHeader:claims:subscriber","dateOfBirth"));
mapSegment("DMG","e03",getInput("claimHeader:claims:subscriber","gender"));
commitSegment("DMG");

// Payer
mapSegment("NM1","e01","PR");
mapSegment("NM1","e02","2");
mapSegment("NM1","e03",getInput("claimHeader:claims:payerInformation","payerName"));
mapSegment("NM1","e08",getInput("claimHeader:claims:payerInformation","claimFillingCode"));
mapSegment("NM1","e09",getInput("claimHeader:claims:payerInformation","payerIdentification"));
commitSegment("NM1");

// Patient
HLCounter++;
mapSegment("HL","e01",snum(HLCounter));
mapSegment("HL","e02",snum(HLCounter - 1));
mapSegment("HL","e03","23");
mapSegment("HL","e04","0");
commitSegment("HL");
mapSegment("PAT","e01","18");
commitSegment("PAT");
mapSegment("NM1","e01","QC");
mapSegment("NM1","e02","1");
mapSegment("NM1","e03",getInput("claimHeader:claims:subscriber","lastNameOrOrganizationName"));
commitSegment("NM1");
mapSegment("CLM","e01",getInput("claimHeader:claims:claimInformation","patientControlNumber"));
mapSegment("CLM","e02",getInput("claimHeader:claims:claimInformation","totalClaimChargeAmount"));
String CLMe05 = getInput("claimHeader:claims:claimInformation","placeOfServiceCode") + ":" + "B" + ":" + getInput("claimHeader:claims:claimInformation","claimFrequencyCode");
mapSegment("CLM","e05",CLMe05);
mapSegment("CLM","e06",getInput("claimHeader:claims:claimInformation","signatureIndicator"));
mapSegment("CLM","e07",getInput("claimHeader:claims:claimInformation","planParticipationCode"));
mapSegment("CLM","e08",getInput("claimHeader:claims:claimInformation","benefitsAssignmentCertificationIndicator"));
mapSegment("CLM","e09",getInput("claimHeader:claims:claimInformation","releaseInformationCode"));
commitSegment("CLM");
mapSegment("NM1","e01","82");
mapSegment("NM1","e02","1");
mapSegment("NM1","e03",getInput("claimHeader:claims:claimInformation:renderingProvider","lastNameOrOrganizationName"));
mapSegment("NM1","e08","XX");
mapSegment("NM1","e09",getInput("claimHeader:claims:claimInformation:renderingProvider","npi"));
commitSegment("NM1");

// Service Line Detail loop
int serviceLinecount = getLoopCount("claimHeader:claims:claimInformation:serviceLines",3);
String path = "claimHeader:claims:claimInformation:serviceLines";
for (int i = 1; i <= serviceLinecount; i++) {
mapSegment("LX","e01",snum(i));
commitSegment("LX");
String compositeVar = getInput(i,path,"professionalService:serviceIdQualifier") + ":" + getInput(i,path,"professionalService:procedureCode");
mapSegment("SV1","e01",compositeVar);
mapSegment("SV1","e02",getInput(i,path,"professionalService:lineItemChargeAmount"));
mapSegment("SV1","e03",getInput(i,path,"professionalService:measurementUnit"));
mapSegment("SV1","e04",getInput(i,path,"professionalService:serviceUnitCount"));
commitSegment("SV1");
mapSegment("DTP","e01","472");
mapSegment("DTP","e02","D8");
mapSegment("DTP","e03",getInput(i,path,"serviceDate"));
commitSegment("DTP");
}
