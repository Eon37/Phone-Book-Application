package Phone_Book;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
public class PhoneRecordController {
    PhoneRecordService phoneRecordService;

    @Autowired
    public PhoneRecordController(PhoneRecordService phoneRecordService) {
        this.phoneRecordService = phoneRecordService;
    }

    @ApiOperation(value = "createRecord", notes = "Create record for user with a specific {id}. User should contain name with length [3; 20]; record should contain number in format: '9(999) 999-9999'",
            response = PhoneRecord.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 400, message = "Validation failed for object='phoneRecord'"),
            @ApiResponse(code = 404, message = "User not found with ID={id}")
    })
    @PostMapping(value = "/api/users/{id}/phone-records")
    public PhoneRecord createRecord(@PathVariable long id, @Valid @RequestBody PhoneRecord phoneRecord) {
        phoneRecordService.savePhoneRecord(id, phoneRecord);
        return phoneRecord;
    }

    @ApiOperation(value = "getById", notes = "Get record with a specific {recordId} from user's with id = {id} phone book", response = PhoneRecord.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 404, message = "User not found with ID={id}"),
            @ApiResponse(code = 404, message = "Record not found with ID={id}")
    })
    @GetMapping(value = "/api/users/{id}/phone-records/{recordId}")
    public PhoneRecord getById(@PathVariable long id, @PathVariable long recordId) {
        return phoneRecordService.getById(id, recordId);
    }

    @ApiOperation(value = "getBookByOwner", notes = "Get phone book from user with id = {id}", response = Iterable.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 404, message = "User not found with ID={id}")
    })
    @GetMapping(path = "/api/users/{id}/phone-records")
    public List<PhoneRecord> getBookByOwner(@PathVariable long id) {
        return phoneRecordService.getBookByOwner(id);
    }

    @ApiOperation(value = "delete", notes = "Delete record with id = {recordId} for user with id = {id}")
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "No Content"),
            @ApiResponse(code = 404, message = "Record not found with ID={recordId}"),
            @ApiResponse(code = 404, message = "User not found with ID={id}")
    })
    @DeleteMapping(value = "/api/users/{id}/phone-records/{recordId}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void delete(@PathVariable long id, @PathVariable long recordId) {
        phoneRecordService.delete(phoneRecordService.getById(id, recordId));
    }

    @ApiOperation(value = "updateRecord", notes = "Update record with id = {recordId} for user with id = {id}. Record should contain number in format: '9(999) 999-9999'")
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "NoContent"),
            @ApiResponse(code = 400, message = "Validation failed for object='updatedRecord'"),
            @ApiResponse(code = 404, message = "Record not found with ID={recordId}"),
            @ApiResponse(code = 404, message = "User not found with ID={id}")
    })
    @PutMapping(value = "/api/users/{id}/phone-records/{recordId}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void updateRecord(@PathVariable long id, @PathVariable long recordId, @Valid @RequestBody PhoneRecord updatedRecord) {
        phoneRecordService.updateRecord(id, recordId, updatedRecord);
    }

    @ApiOperation(value = "getRecordByNumber", notes = "get record that contains specified number (format: '9(999) 999-9999') for user with id = {id}",
            response = PhoneRecord.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 400, message = "Incorrect number format."),
            @ApiResponse(code = 404, message = "User not found with ID={id}"),
            @ApiResponse(code = 404, message = "Record not found with number=Param(\"number\")"),
    })
    @GetMapping(value = "/api/users/{id}/phone-records/search")
    public PhoneRecord getRecordByNumber(@PathVariable long id, @RequestParam String number) {
        return phoneRecordService.getRecordByNumber(id, number);
    }
}
