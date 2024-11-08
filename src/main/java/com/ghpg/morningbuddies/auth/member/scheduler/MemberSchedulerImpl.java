package com.ghpg.morningbuddies.auth.member.scheduler;

import java.time.LocalDateTime;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ghpg.morningbuddies.auth.member.repository.MemberRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberSchedulerImpl implements MemberScheduler {

	private final MemberRepository memberRepository;
	private static final int RETENTION_HOURS = 24; // 삭제 마크 후 24시간 유지

	@Scheduled(cron = "0 0 * * * *") // 매 시간 0분 0초에 실행
	@Transactional
	public void cleanupDeletedMembers() {
		try {
			LocalDateTime deletionCutoff = LocalDateTime.now().minusHours(RETENTION_HOURS);

			int deletedCount = memberRepository.deleteAllByIsDeletedTrueAndUpdatedAtBefore(deletionCutoff);

			if (deletedCount > 0) {
				log.info("Cleaned up {} deleted member(s) marked for deletion before {}",
					deletedCount, deletionCutoff);
			} else {
				log.debug("No deleted members found for cleanup before {}", deletionCutoff);
			}
		} catch (Exception e) {
			log.error("Failed to cleanup deleted members", e);
			throw new RuntimeException("Failed to cleanup deleted members", e);
		}
	}
}