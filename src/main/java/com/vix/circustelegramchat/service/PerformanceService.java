package com.vix.circustelegramchat.service;

import com.vix.circustelegramchat.model.Performance;
import com.vix.circustelegramchat.repository.PerformanceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * This Service is responsible for communicate with matched repository.
 * Getting data from repository, preparing and sending to user.
 */
@Service
@RequiredArgsConstructor
public class PerformanceService {

    private final PerformanceRepository performanceRepository;

    /**
     * This method is responsible for a simple search by id
     *
     * @param id - ID of the performance we are looking for
     * @return Found performance if present, or just created empty one.
     */
    public Performance findById(int id) {
        return performanceRepository.findById(id).orElse(Performance.builder().build());
    }

    /**
     * This method is responsible for finding all performances on a specific day.
     *
     * @param date - Date of the performances
     * @return List of found performances
     */
    public List<Performance> getPerformancesByDate(LocalDate date) {
        return performanceRepository.findAll()
                .stream()
                .filter(s -> s.getDate().equals(date))
                .toList();
    }

    /**
     * This method finds the nearest date of upcoming performances
     *
     * @param referenceDate - Date from which the search begin
     * @return Optional of found date or empty optional
     */
    public Optional<LocalDate> getNextPerformanceDate(LocalDate referenceDate) {
        return performanceRepository.findAll(Sort.by("date"))
                .stream()
                .map(Performance::getDate)
                .filter(s -> s.isAfter(referenceDate))
                .min(LocalDate::compareTo);
    }

    /**
     * This method finds the nearest date of previous performances
     *
     * @param referenceDate - Date from which the search begin
     * @return Optional of found date or empty optional
     */
    public Optional<LocalDate> getPreviousPerformanceDate(LocalDate referenceDate) {
        return performanceRepository.findAll(Sort.by("date"))
                .stream()
                .map(Performance::getDate)
                .filter(s -> s.isBefore(referenceDate))
                .max(LocalDate::compareTo);
    }
}
