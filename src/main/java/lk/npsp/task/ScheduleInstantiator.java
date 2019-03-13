package lk.npsp.task;

import lk.npsp.domain.ScheduleInstance;
import lk.npsp.repository.ScheduleInstanceRepository;
import lk.npsp.repository.ScheduleTemplateRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.Date;

@Component
public class ScheduleInstantiator {

    private final Logger log = LoggerFactory.getLogger(ScheduleInstantiator.class);
    private final ScheduleInstanceRepository scheduleInstanceRepository;
    private final ScheduleTemplateRepository scheduleTemplateRepository;

    public ScheduleInstantiator(ScheduleInstanceRepository scheduleInstanceRepository,
                                    ScheduleTemplateRepository scheduleTemplateRepository) {

        this.scheduleInstanceRepository = scheduleInstanceRepository;
        this.scheduleTemplateRepository = scheduleTemplateRepository;
    }


//    @Scheduled(cron = "0 5 0 1/1 * ? *")
    @Scheduled(fixedRate = 20000)
    public void instantiateSchedules() {
        log.info("Starting Schedule Instantiator");

//        find all active schedule templates for day of week
//        check if instance already exist in the current day for given template
//        if not create a new instance from template
        ScheduleInstance scheduleInstance= new ScheduleInstance();
        scheduleInstance.setDate(LocalDate.now());
        scheduleInstanceRepository.save(scheduleInstance);
    }

}
