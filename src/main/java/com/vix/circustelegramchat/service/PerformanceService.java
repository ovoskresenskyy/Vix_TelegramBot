package com.vix.circustelegramchat.service;

import com.vix.circustelegramchat.model.Performance;
import com.vix.circustelegramchat.repository.PerformanceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PerformanceService {

    private final PerformanceRepository performanceRepository;

    public List<Performance> getPerformancesByDate(LocalDate date) {
        return performanceRepository.findAll()
                .stream()
                .filter(s -> s.getDate().equals(date))
                .toList();
    }

    public Optional<LocalDate> getNextPerformanceDate(LocalDate referenceDate) {
        return performanceRepository.findAll(Sort.by("date"))
                .stream()
                .map(Performance::getDate)
                .filter(s -> s.isAfter(referenceDate))
                .min(LocalDate::compareTo);
    }

    public Optional<LocalDate> getUpcomingPerformanceDate() {
        return getNextPerformanceDate(LocalDate.now());
    }

    public Optional<LocalDate> getPreviousPerformanceDate(LocalDate referenceDate) {
        return performanceRepository.findAll(Sort.by("date"))
                .stream()
                .map(Performance::getDate)
                .filter(s -> s.isBefore(referenceDate))
                .max(LocalDate::compareTo);
    }

    public Performance findById(int id) {
        return performanceRepository.findById(id).orElse(Performance.builder().build());
    }
}
