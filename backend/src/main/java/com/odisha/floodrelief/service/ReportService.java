package com.odisha.floodrelief.service;

import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.Font;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;
import com.odisha.floodrelief.entity.AuditLog;
import com.odisha.floodrelief.entity.Campaign;
import com.odisha.floodrelief.entity.Donation;
import com.odisha.floodrelief.entity.FloodReport;
import com.odisha.floodrelief.entity.Member;
import com.odisha.floodrelief.entity.Notification;
import com.odisha.floodrelief.entity.Payment;
import com.odisha.floodrelief.entity.Volunteer;
import com.odisha.floodrelief.repository.*;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReportService {

    private final DonationRepository donationRepository;
    private final MemberRepository memberRepository;
    private final VolunteerRepository volunteerRepository;
    private final CampaignRepository campaignRepository;
    private final FloodReportRepository floodReportRepository;
    private final PaymentRepository paymentRepository;
    private final AuditLogRepository auditLogRepository;
    private final NotificationRepository notificationRepository;

    public byte[] exportDonationsExcel() throws Exception {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Donations");
        Row header = sheet.createRow(0);
        String[] columns = {"Donation ID", "Donor", "Amount", "Status", "Campaign", "Date"};
        for (int i = 0; i < columns.length; i++) {
            header.createCell(i).setCellValue(columns[i]);
        }

        int rowIdx = 1;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
        List<Donation> donations = donationRepository.findAll();
        for (Donation donation : donations) {
            Row row = sheet.createRow(rowIdx++);
            row.createCell(0).setCellValue(donation.getDonationId());
            row.createCell(1).setCellValue(donation.getDonorName());
            row.createCell(2).setCellValue(donation.getAmount().doubleValue());
            row.createCell(3).setCellValue(donation.getStatus().name());
            row.createCell(4).setCellValue(donation.getCampaign() != null ? donation.getCampaign().getTitle() : "General");
            row.createCell(5).setCellValue(donation.getCreatedAt() != null ? donation.getCreatedAt().format(formatter) : "");
        }

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        workbook.write(out);
        workbook.close();
        return out.toByteArray();
    }

    public byte[] exportMembersExcel() throws Exception {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Members");
        Row header = sheet.createRow(0);
        String[] columns = {"Membership ID", "Name", "Email", "Status", "Valid Until"};
        for (int i = 0; i < columns.length; i++) {
            header.createCell(i).setCellValue(columns[i]);
        }
        int rowIdx = 1;
        for (Member member : memberRepository.findAll()) {
            Row row = sheet.createRow(rowIdx++);
            row.createCell(0).setCellValue(member.getMembershipId() != null ? member.getMembershipId() : "");
            row.createCell(1).setCellValue(member.getUser() != null ? member.getUser().getFullName() : "");
            row.createCell(2).setCellValue(member.getUser() != null ? member.getUser().getEmail() : "");
            row.createCell(3).setCellValue(member.getStatus() != null ? member.getStatus().name() : "");
            row.createCell(4).setCellValue(member.getValidUntil() != null ? member.getValidUntil().toString() : "");
        }
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        workbook.write(out);
        workbook.close();
        return out.toByteArray();
    }

    public byte[] exportVolunteersExcel() throws Exception {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Volunteers");
        Row header = sheet.createRow(0);
        String[] columns = {"Volunteer ID", "Name", "Email", "Status", "Area", "District", "Work Status"};
        for (int i = 0; i < columns.length; i++) {
            header.createCell(i).setCellValue(columns[i]);
        }
        int rowIdx = 1;
        for (Volunteer volunteer : volunteerRepository.findAll()) {
            Row row = sheet.createRow(rowIdx++);
            row.createCell(0).setCellValue(volunteer.getVolunteerId() != null ? volunteer.getVolunteerId() : "");
            row.createCell(1).setCellValue(volunteer.getUser() != null ? volunteer.getUser().getFullName() : "");
            row.createCell(2).setCellValue(volunteer.getUser() != null ? volunteer.getUser().getEmail() : "");
            row.createCell(3).setCellValue(volunteer.getStatus() != null ? volunteer.getStatus().name() : "");
            row.createCell(4).setCellValue(volunteer.getAssignedArea() != null ? volunteer.getAssignedArea() : "");
            row.createCell(5).setCellValue(volunteer.getAssignedDistrict() != null ? volunteer.getAssignedDistrict() : "");
            row.createCell(6).setCellValue(volunteer.getWorkStatus() != null ? volunteer.getWorkStatus().name() : "");
        }
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        workbook.write(out);
        workbook.close();
        return out.toByteArray();
    }

    public byte[] exportCampaignsExcel() throws Exception {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Campaigns");
        Row header = sheet.createRow(0);
        String[] columns = {"Title", "Target", "Collected", "Status", "Start", "End"};
        for (int i = 0; i < columns.length; i++) {
            header.createCell(i).setCellValue(columns[i]);
        }
        int rowIdx = 1;
        for (Campaign campaign : campaignRepository.findAll()) {
            Row row = sheet.createRow(rowIdx++);
            row.createCell(0).setCellValue(campaign.getTitle());
            row.createCell(1).setCellValue(campaign.getTargetAmount() != null ? campaign.getTargetAmount().doubleValue() : 0);
            row.createCell(2).setCellValue(campaign.getCollectedAmount() != null ? campaign.getCollectedAmount().doubleValue() : 0);
            row.createCell(3).setCellValue(campaign.getStatus() != null ? campaign.getStatus().name() : "");
            row.createCell(4).setCellValue(campaign.getStartDate() != null ? campaign.getStartDate().toString() : "");
            row.createCell(5).setCellValue(campaign.getEndDate() != null ? campaign.getEndDate().toString() : "");
        }
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        workbook.write(out);
        workbook.close();
        return out.toByteArray();
    }

    public byte[] exportFloodReportsExcel() throws Exception {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Flood Reports");
        Row header = sheet.createRow(0);
        String[] columns = {"ID", "Village", "District", "Urgency", "Status", "Date"};
        for (int i = 0; i < columns.length; i++) {
            header.createCell(i).setCellValue(columns[i]);
        }
        int rowIdx = 1;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
        for (FloodReport report : floodReportRepository.findAll()) {
            Row row = sheet.createRow(rowIdx++);
            row.createCell(0).setCellValue(report.getId());
            row.createCell(1).setCellValue(report.getVillage());
            row.createCell(2).setCellValue(report.getDistrict());
            row.createCell(3).setCellValue(report.getUrgency() != null ? report.getUrgency().name() : "");
            row.createCell(4).setCellValue(report.getStatus() != null ? report.getStatus().name() : "");
            row.createCell(5).setCellValue(report.getCreatedAt() != null ? report.getCreatedAt().format(formatter) : "");
        }
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        workbook.write(out);
        workbook.close();
        return out.toByteArray();
    }

    public byte[] exportDonationsPdf() throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4);
        PdfWriter.getInstance(document, baos);
        document.open();
        Font titleFont = new Font(Font.HELVETICA, 16, Font.BOLD);
        Font normalFont = new Font(Font.HELVETICA, 10, Font.NORMAL);
        document.add(new Paragraph("Odisha Flood Relief - Donation Report", titleFont));
        document.add(Chunk.NEWLINE);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
        for (Donation donation : donationRepository.findAll()) {
            document.add(new Paragraph(
                    donation.getDonationId() + " | " + donation.getDonorName()
                            + " | Rs." + donation.getAmount() + " | " + donation.getStatus()
                            + " | " + (donation.getCreatedAt() != null ? donation.getCreatedAt().format(formatter) : ""),
                    normalFont));
        }
        document.close();
        return baos.toByteArray();
    }

    public Page<Payment> getAllPayments(Pageable pageable) {
        return paymentRepository.findAllByOrderByCreatedAtDesc(pageable);
    }

    public Page<AuditLog> getAuditLogs(Pageable pageable) {
        return auditLogRepository.findAllByOrderByCreatedAtDesc(pageable);
    }

    public Page<Notification> getMyNotifications(Pageable pageable, Long userId) {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable);
    }
}
