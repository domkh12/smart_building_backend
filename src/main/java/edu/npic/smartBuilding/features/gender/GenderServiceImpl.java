package edu.npic.smartBuilding.features.gender;

import edu.npic.smartBuilding.domain.Gender;
import edu.npic.smartBuilding.features.gender.dto.GenderResponse;
import edu.npic.smartBuilding.mapper.GenderMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GenderServiceImpl implements GenderService{

    private final GenderRepository genreRepository;
    private final GenderMapper genderMapper;

    @Override
    public List<GenderResponse> findAll() {
        List<Gender> genders = genreRepository.findAll();
        return genders.stream().map(gender -> genderMapper.toGenderResponse(gender)).toList();
    }
}
