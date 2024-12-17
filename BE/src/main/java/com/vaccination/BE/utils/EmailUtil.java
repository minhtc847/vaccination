package com.vaccination.BE.utils;

import com.vaccination.BE.dto.request.employee_request.EmailDto;
import com.vaccination.BE.entity.PasswordResetToken;
import com.vaccination.BE.entity.VaccineEmployee;
import com.vaccination.BE.entity.VaccineInjectionSchedule;
import com.vaccination.BE.excepiton.exceptions.APIException;
import com.vaccination.BE.repository.PasswordResetTokenRepository;
import com.vaccination.BE.service.PasswordResetTokenService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class EmailUtil {

    @Autowired
    TokenEmailUtil tokenEmailUtil;

    @Autowired
    private JavaMailSender javaMailSender;

    @Autowired
    private PasswordResetTokenService tokenService;

    @Autowired
    PasswordResetTokenRepository passwordResetTokenRepository;
    @Async
    public void sendSetPassword(String email) throws MessagingException {
        PasswordResetToken token = tokenService.createToken(email);

        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
        mimeMessageHelper.setTo(email);
        mimeMessageHelper.setSubject("[Vaccine Management] Password Reset Request");

        String emailContent = """
                <html>
                <head>
                    <style>
                        .btn {
                            display: inline-block;
                            background-color: white;
                            padding: 10px 20px;
                            border-radius: 5px;
                            border: 1px solid transparent;
                            transition: background-color 0.3s ease;
                        }
                        .btn:hover {
                            background-color: gray;
                        }
                        .btn:active {
                            background-color: #1e7e34;
                        }
                        .btn:focus {
                            outline: none;
                            box-shadow: 0 0 0 0.2rem rgba(40, 167, 69, 0.5);
                        }
                    </style>
                </head>
                <body>
                    <div class="email-container">
                        <p>You recently requested to reset your password for your account. Use the button below to reset it. This password reset is only valid for the next 24 hours.</p>
                        <a href="http://localhost:3000/set-password?token=%s" class="btn" target="_blank">Reset Password</a>
                        <p>If you did not make this request, please ignore this email.</p>
                    </div>
                </body>
                </html>
                """.formatted(token.getToken());

        mimeMessageHelper.setText(emailContent, true);
        javaMailSender.send(mimeMessage);
    }

    @Async
    public void sendEmail(EmailDto emailDto) throws MessagingException {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
        mimeMessageHelper.setTo(emailDto.getTo());
        mimeMessageHelper.setSubject(emailDto.getSubject());
        tokenService.createToken(emailDto.getTo());
        if(passwordResetTokenRepository.findByEmailAndUsed(emailDto.getTo(),false)==null)
        {
            throw new APIException(HttpStatus.NOT_FOUND,"Not Found Token");
        }
        String token = passwordResetTokenRepository.findByEmailAndUsed(emailDto.getTo(),false);
        String name = emailDto.getProps().get("name").toString();

        String username = emailDto.getProps().get("username").toString();
        String password = emailDto.getProps().get("password").toString();

        String emailContent = String.format("""
            <html>
                <head>
                    <style>
                        body{
                            font-family:'Roboto',sans-serif;
                            font-size:48px;
                        }
                    </style>
                </head>
                <body>
                    <div class="email-container">
                        <p>Xin chào %s,</p>
                        <p>Chúng tôi gửi thông tin truy cập hệ thống của bạn:</p>
                        <p>Tên đăng nhập: %s</p>
                        <p>Mật khẩu: %s</p>
                        <p>Nếu bạn muốn đổi mật khẩu, vui lòng truy cập đường link bên dưới:</p>
                        <a href="http://localhost:3000/set-password?token=%s" class="btn" target="_blank">Change Password</a>
                        <p>If you did not make this request, please ignore this email.</p>
                    </div>
                </body>
            </html>
        """, name, username, password, token);

        mimeMessageHelper.setText(emailContent, true);
        javaMailSender.send(mimeMessage);
    }

    @Async
    public void sendEmailRegisterInject(EmailDto emailDto) {
        try {
            // Tạo đối tượng MimeMessage
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            // Cấu hình MimeMessageHelper
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            // Cài đặt thông tin email
            mimeMessageHelper.setTo(emailDto.getTo());
            mimeMessageHelper.setSubject(emailDto.getSubject());

            // Lấy thông tin từ emailDto
            VaccineEmployee cus = (VaccineEmployee) emailDto.getProps().get("cus");
            VaccineInjectionSchedule schedule = (VaccineInjectionSchedule) emailDto.getProps().get("schedule");
            if (cus == null || schedule == null) {
                throw new IllegalArgumentException("Missing required information in emailDto properties");
            }

            // Chuẩn bị thông tin email
            String name = cus.getUsername();
            String vaccineName = schedule.getVaccine().getVaccineName();
            int time = schedule.getInjectionTimes();
            LocalDate from = schedule.getStartDate();
            LocalDate to = schedule.getEndDate();
            String token = tokenEmailUtil.generateToken(name, schedule.getId());

            // Tạo nội dung email
            String emailContent = String.format("""
                        <html>
                            <head>
                                <style>
                                    body {
                                        font-family: 'Roboto', sans-serif;
                                        font-size: 16px;
                                    }
                                    .email-container {
                                        max-width: 600px;
                                        margin: auto;
                                        padding: 20px;
                                        border: 1px solid #ddd;
                                        border-radius: 5px;
                                    }
                                    .btn {
                                        display: inline-block;
                                        padding: 10px 20px;
                                        font-size: 16px;
                                        color: #fff;
                                        background-color: #007bff;
                                        text-decoration: none;
                                        border-radius: 5px;
                                    }
                                </style>
                            </head>
                            <body>
                                <div class="email-container">
                                    <p>Hello %s,</p>
                                    <p>We are sending you information about the vaccination schedule for vaccine %s, scheduled %s times from %s to %s.</p>
                                    <p>If you would like to register for vaccination, please click the link below:</p>
                                    <a href="http://localhost:3000/register-inject?token=%s" class="btn" target="_blank">REGISTER</a>
                                    <p>If you did not make this request, please ignore this email.</p>
                                </div>
                            </body>
                        </html>
                    """, name, vaccineName, time, from, to, token);

            // Cài đặt nội dung email
            mimeMessageHelper.setText(emailContent, true);

            // Gửi email
            javaMailSender.send(mimeMessage);
        } catch (MessagingException e) {
            // Xử lý lỗi gửi email
            e.printStackTrace();
            // Ghi log lỗi hoặc xử lý lỗi thêm nếu cần
        } catch (Exception e) {
            // Xử lý lỗi khác
            e.printStackTrace();
            // Ghi log lỗi hoặc xử lý lỗi thêm nếu cần
        }
    }
}
