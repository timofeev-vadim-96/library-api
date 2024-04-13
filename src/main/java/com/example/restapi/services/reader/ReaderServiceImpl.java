package com.example.restapi.services.reader;

import com.example.restapi.dao.ReaderRepository;
import com.example.restapi.models.appEntities.ReaderEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReaderServiceImpl implements ReaderService {
    private final ReaderRepository dao;

    @Override
    public ReaderEntity findById(long id){
        return dao.findById(id).orElse(null);
    }

    @Override
    public ReaderEntity save(ReaderEntity readerEntity){
        return dao.save(readerEntity);
    }

    @Override
    public boolean deleteById(long id){
        ReaderEntity reader = findById(id);
        if (reader == null) return false;
        else {
            dao.deleteById(id);
            return true;
        }
    }

    @Override
    public List<ReaderEntity> findAll() {
        return dao.findAll();
    }
    @Override
    public ReaderEntity findByPhone(String phone){
        return dao.findByPhone(phone);
    }
    @Override
    public List<ReaderEntity> findAllBySecondName(String secondName){
        return dao.findAllBySecondName(secondName);
    }

    @Override
    public List<ReaderEntity> findAllByBirthDayAfter(LocalDate date){
        return dao.findAllByBirthDayAfter(date);
    }
}
