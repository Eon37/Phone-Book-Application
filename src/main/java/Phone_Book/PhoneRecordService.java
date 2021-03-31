package Phone_Book;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class PhoneRecordService {
    PhoneRecordRepository phoneRecordRepository;
    UserService userService;

    @Autowired
    public PhoneRecordService(PhoneRecordRepository phoneRecordRepository, UserService userService) {
        this.phoneRecordRepository = phoneRecordRepository;
        this.userService = userService;
    }

    public void savePhoneRecord(long ownerId, PhoneRecord phoneRecord) {
        User user = userService.getById(ownerId);
        phoneRecord.setOwnerId(user);
        phoneRecordRepository.save(phoneRecord);
    }

    public PhoneRecord getById(long ownerId, long recordId) {
        return getBookByOwner(ownerId)
                .stream()
                .filter(x -> x.getId() == recordId)
                .limit(1)
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Record not found with ID=" + recordId));
    }

    public void delete(PhoneRecord phoneRecord) {
        phoneRecordRepository.delete(phoneRecord);
    }


    public PhoneRecord getRecordByNumber(long ownerId, String number) {
        if(!number.matches(Constants.REGEXP_PHONE)) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Incorrect number format.");
        return getBookByOwner(ownerId)
                .stream()
                .filter(x -> x.getNumber().equals(number))
                .limit(1)
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Record not found with number=" + number));
    }

    public void updateRecord(long ownerId, long recordId, PhoneRecord updatedRecord) {
        PhoneRecord phoneRecord = getById(ownerId, recordId);

        phoneRecord.setName(updatedRecord.getName());
        phoneRecord.setNumber(updatedRecord.getNumber());

        savePhoneRecord(ownerId, phoneRecord);
    }

    public List<PhoneRecord> getBookByOwner(long id) {
        return userService.getBookByOwner(id);
    }
}
