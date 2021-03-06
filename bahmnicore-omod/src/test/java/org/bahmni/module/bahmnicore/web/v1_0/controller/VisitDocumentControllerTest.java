package org.bahmni.module.bahmnicore.web.v1_0.controller;

import org.bahmni.module.bahmnicore.model.Document;
import org.bahmni.module.bahmnicore.service.PatientDocumentService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openmrs.Patient;
import org.openmrs.Visit;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.module.bahmniemrapi.document.contract.VisitDocumentRequest;
import org.openmrs.module.bahmniemrapi.document.service.VisitDocumentService;
import org.openmrs.module.bahmniemrapi.visitlocation.BahmniVisitLocationService;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

@PrepareForTest(Context.class)
@RunWith(PowerMockRunner.class)
public class VisitDocumentControllerTest {
    @InjectMocks
    VisitDocumentController visitDocumentController;
    @Mock
    PatientService patientService;
    @Mock
    AdministrationService administrationService;
    @Mock
    PatientDocumentService patientDocumentService;
    @Mock
    VisitDocumentService visitDocumentService;
    @Mock
    BahmniVisitLocationService bahmniVisitLocationService;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void shouldGetDefaultEncounterTypeIfNoEncounterTypeIsPassedInRequest() throws Exception {
        PowerMockito.mockStatic(Context.class);
        PowerMockito.when(Context.getPatientService()).thenReturn(patientService);
        Patient patient = new Patient();
        patient.setId(1);
        patient.setUuid("patient-uuid");
        when(patientService.getPatientByUuid("patient-uuid")).thenReturn(patient);
        when(administrationService.getGlobalProperty("bahmni.encounterType.default")).thenReturn("consultation");

        Document document = new Document("abcd", "jpeg", null, "patient-uuid", "image");

        visitDocumentController.saveDocument(document);

        verify(patientDocumentService).saveDocument(1, "consultation", "abcd", "jpeg", document.getFileType());
        verify(administrationService).getGlobalProperty("bahmni.encounterType.default");
    }

    @Test
    public void shouldNotGetDefaultEncounterTypeIfEncounterTypeIsPassedInRequest() throws Exception {
        PowerMockito.mockStatic(Context.class);
        PowerMockito.when(Context.getPatientService()).thenReturn(patientService);
        Patient patient = new Patient();
        patient.setId(1);
        patient.setUuid("patient-uuid");
        when(patientService.getPatientByUuid("patient-uuid")).thenReturn(patient);
        when(administrationService.getGlobalProperty("bahmni.encounterType.default")).thenReturn("consultation");

        Document document = new Document("abcd", "jpeg", "radiology", "patient-uuid", "image");

        visitDocumentController.saveDocument(document);

        verify(patientDocumentService).saveDocument(1, "radiology", "abcd", "jpeg", document.getFileType());
        verifyZeroInteractions(administrationService);
    }

    @Test
    public void shouldSetVisitLocationUuid() throws Exception {
        Visit visit = new Visit();
        visit.setUuid("visit-uuid");
        VisitDocumentRequest visitDocumentRequest = new VisitDocumentRequest("patient-uuid", "visit-uuid", "visit-type-uuid",
                null, null, "encounter-uuid", null, null, "provider-uuid", "location-uuid", null);

        when(visitDocumentService.upload(visitDocumentRequest)).thenReturn(visit);

        when(bahmniVisitLocationService.getVisitLocationUuid("location-uuid")).thenReturn("VisitLocationuuid");
        visitDocumentController.save(visitDocumentRequest);

        verify(bahmniVisitLocationService).getVisitLocationUuid("location-uuid");
    }
}