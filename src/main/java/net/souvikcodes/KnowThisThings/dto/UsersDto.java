package net.souvikcodes.KnowThisThings.dto;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsersDto {
    private String username;
    private String password;
    private Boolean adminFlag ;
    private List<String> journalEntries = new ArrayList<>();
}
