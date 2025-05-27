package edu.npic.smartBuilding.features.analysis;

import edu.npic.smartBuilding.features.analysis.dto.AnalysisResponse;
import edu.npic.smartBuilding.features.analysis.dto.PowerAnalysisResponse;
import edu.npic.smartBuilding.features.analysis.dto.TotalCountResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/analysis")
@RequiredArgsConstructor
public class AnalysisController {
    private final AnalysisService analysisService;

    @PreAuthorize("hasAnyAuthority('ROLE_MANAGER', 'ROLE_ADMIN')")
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    AnalysisResponse getAnalysis(@RequestParam LocalDate date_from,
                                 @RequestParam LocalDate date_to) {
        return analysisService.getAnalysis(date_from, date_to);
    }

    @PreAuthorize("hasAnyAuthority('ROLE_MANAGER', 'ROLE_ADMIN')")
    @GetMapping("/total-counts")
    @ResponseStatus(HttpStatus.OK)
    TotalCountResponse getTotalCounts() {
        return analysisService.getTotalCounts();
    }

    @PreAuthorize("hasAnyAuthority('ROLE_MANAGER', 'ROLE_ADMIN')")
    @GetMapping("/power-usage")
    @ResponseStatus(HttpStatus.OK)
    PowerAnalysisResponse getPowerAnalysis(
            @RequestParam(required = false, defaultValue = "24h")String range
            ) {
        return analysisService.getPowerAnalysis(range);
    }

}
