package com.tecknobit.gluky.configuration;

import com.tecknobit.apimanager.annotations.Wrapper;
import com.tecknobit.equinoxbackend.resourcesutils.ResourcesManager;
import org.springframework.web.multipart.MultipartFile;

import static com.tecknobit.glukycore.ConstantsKt.REPORTS_KEY;

public interface GlukyResourcesManager extends ResourcesManager {

    @Wrapper
    default String createReport(MultipartFile resource, String reportId) {
        return createResource(resource, REPORTS_KEY, reportId);
    }

    @Wrapper
    default boolean deleteReportResource(String reportId) {
        return deleteResource(REPORTS_KEY, reportId);
    }

}
