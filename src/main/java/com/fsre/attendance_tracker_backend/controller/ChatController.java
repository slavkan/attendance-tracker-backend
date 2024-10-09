package com.fsre.attendance_tracker_backend.controller;

import com.fsre.attendance_tracker_backend.service.ClassAttendanceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fsre.attendance_tracker_backend.model.Message;
import com.fsre.attendance_tracker_backend.model.dto.StudentArrivalDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ChatController {

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    @Autowired
    private ClassAttendanceService classAttendanceService;

    Logger logger = LoggerFactory.getLogger(ChatController.class);

    @MessageMapping("/message")
    @SendTo("/chatroom/public")
    public Message receiveMessage(@Payload Message message){
        Logger logger = LoggerFactory.getLogger(ChatController.class);
        return message;
    }

    @MessageMapping("/private-message")
    public Message recMessage(@Payload Message message){
        simpMessagingTemplate.convertAndSendToUser(message.getReceiverName(),"/private",message);
        return message;
    }


    /*@MessageMapping("/class-session")
    public StudentArrivalDto receiveMessage(@Payload StudentArrivalDto studentArrivalDto) {
        logger.info("SessionId: " + studentArrivalDto.getClassSessionId());

        try {
            simpMessagingTemplate.convertAndSend("/topic/class-session/" + studentArrivalDto.getClassSessionId(), studentArrivalDto);
        } catch (Exception e) {
            logger.error("Failed to send message to /topic/class-session/" + studentArrivalDto.getClassSessionId(), e);
        }

        classAttendanceService.helloWorld();

        return studentArrivalDto;
    }*/


    @MessageMapping("/class-session")
    public StudentArrivalDto receiveMessage(@Payload StudentArrivalDto studentArrivalDto) {
        logger.info("SessionId: " + studentArrivalDto.getClassSessionId());

        simpMessagingTemplate.convertAndSendToUser(studentArrivalDto.getClassSessionId().toString(),"/private", studentArrivalDto);

        classAttendanceService.helloWorld();

        return studentArrivalDto;
    }

}

