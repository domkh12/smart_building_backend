package edu.npic.smartBuilding.features.analysis;

import edu.npic.smartBuilding.features.analysis.dto.PowerAnalysisResponse;
import edu.npic.smartBuilding.features.analysis.dto.TotalCountResponse;

import java.time.LocalDateTime;
import java.util.List;

public interface AnalysisService {
    TotalCountResponse getTotalCounts();

    PowerAnalysisResponse getPowerAnalysis(String rang);
}
