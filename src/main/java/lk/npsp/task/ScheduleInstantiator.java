package lk.npsp.task;

import lk.npsp.domain.ScheduleInstance;
import lk.npsp.domain.ScheduleTemplate;
import lk.npsp.domain.Weekday;
import lk.npsp.domain.enumeration.ScheduleState;
import lk.npsp.domain.enumeration.Weekdays;
import lk.npsp.repository.ScheduleInstanceRepository;
import lk.npsp.repository.ScheduleTemplateRepository;
import lk.npsp.repository.WeekdayRepository;
import lk.npsp.service.ScheduleInstanceManager;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.time.ZoneId;

@Component
public class ScheduleInstantiator {

    private final Logger log = LoggerFactory.getLogger(ScheduleInstantiator.class);
    private final ScheduleInstanceRepository scheduleInstanceRepository;
    private final ScheduleTemplateRepository scheduleTemplateRepository;
    private final ScheduleInstanceManager scheduleInstanceManager;

    public ScheduleInstantiator(ScheduleInstanceRepository scheduleInstanceRepository,
                                ScheduleTemplateRepository scheduleTemplateRepository,
                                ScheduleInstanceManager scheduleInstanceManager) {

        this.scheduleInstanceRepository = scheduleInstanceRepository;
        this.scheduleTemplateRepository = scheduleTemplateRepository;
        this.scheduleInstanceManager = scheduleInstanceManager;
    }


    /**
     * create instances of schedule templates for the day
     * delay set to 1 hour
     */
    @Scheduled(fixedDelay = 3600000)
    public void instantiateSchedules() {
        log.info("Starting Schedule Instantiator");

        createScheduleInstances(LocalDate.now());
        createScheduleInstances(LocalDate.now().plusDays(1));

    }

    /**
     * create schedule instances from schedule templates for a given date
     */
    private void createScheduleInstances(LocalDate date){

        Date datetime = Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant());
        SimpleDateFormat simpleDateformat = new SimpleDateFormat("EEEE"); // the day of the week spelled out completely
        String DayOfWeekString = simpleDateformat.format(datetime);
        Weekdays DayOfWeekEnum = Weekdays.valueOf(DayOfWeekString);

        log.info(DayOfWeekEnum.toString());
        List<ScheduleTemplate> scheduleTemplateList = scheduleTemplateRepository.findAllActiveTemplates();
        List<ScheduleInstance> scheduleInstanceList = scheduleInstanceRepository.
            findScheduleInstancesListByDate(date);
        log.info("Active templates found {}", scheduleTemplateList.size());
        log.info("Instances found {}", scheduleInstanceList.size());
        for (ScheduleTemplate scheduleTemplate : scheduleTemplateList
        ) {
            boolean alreadyExistsForTemplate = ScheduleInstanceAlreadyExistsForTemplate(scheduleInstanceList, scheduleTemplate);
            if (getWeekdayEnums(scheduleTemplate.getWeekdays()).contains(DayOfWeekEnum) && !alreadyExistsForTemplate) {

                log.info("Creating instance for template {}", scheduleTemplate.getId());
                ScheduleInstance scheduleInstance = new ScheduleInstance();
                scheduleInstance.setDate(date);
                scheduleInstance.setScheduleState(ScheduleState.PENDING);
                scheduleInstance = scheduleInstanceManager.createFromTemplate(scheduleInstance, scheduleTemplate);
                scheduleInstanceRepository.save(scheduleInstance);
            }
        }
    }


    /**
     * check if schedule instance exists for a given template in a given list
     *
     * @param scheduleInstancesList
     * @param scheduleTemplate
     * @return
     */
    private boolean ScheduleInstanceAlreadyExistsForTemplate(List<ScheduleInstance> scheduleInstancesList,
                                                             ScheduleTemplate scheduleTemplate) {

        if (scheduleInstancesList.size() == 0) {
            return false;
        }
        boolean scheduleInstanceExists = false;
        for (ScheduleInstance scheduleInstance : scheduleInstancesList
        ) {
            if (scheduleInstance.getScheduleTemplate().getId().equals(scheduleTemplate.getId())) {
                scheduleInstanceExists = true;
                break;
            }
        }

        return scheduleInstanceExists;
    }

    /**
     * @param weekdays
     * @return
     */
    public Set<Weekdays> getWeekdayEnums(Set<Weekday> weekdays) {
        Set<Weekdays> weekdayEnums = new HashSet<>();
        for (Weekday weekday : weekdays
        ) {
            weekdayEnums.add(weekday.getWeekday());
        }
        return weekdayEnums;
    }

}
