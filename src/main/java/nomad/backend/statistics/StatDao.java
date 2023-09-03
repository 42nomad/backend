package nomad.backend.statistics;


import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Date;

@Getter
@AllArgsConstructor
public class StatDao {
    Date startDate;

    Date endDate;

    int sort;
}