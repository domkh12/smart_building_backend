package edu.npic.smartBuilding.features.analysis;

import edu.npic.smartBuilding.features.analysis.dto.AnalysisResponse;
import edu.npic.smartBuilding.features.analysis.dto.PowerAnalysisResponse;
import edu.npic.smartBuilding.features.analysis.dto.TotalCountResponse;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface AnalysisService {
    AnalysisResponse getAnalysis(LocalDate dateFrom, LocalDate dateTo);

    TotalCountResponse getTotalCounts();

    PowerAnalysisResponse getPowerAnalysis(String rang);
}
