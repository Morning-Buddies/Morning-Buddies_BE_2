package com.ghpg.morningbuddies.domain.gameroom.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import com.ghpg.morningbuddies.domain.gamesession.entity.GameSession;
import com.ghpg.morningbuddies.domain.gametype.entity.GameType;
import com.ghpg.morningbuddies.domain.groups.entity.Groups;
import com.ghpg.morningbuddies.global.common.BaseEntity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@DynamicInsert
@DynamicUpdate
public class GameRoom extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "game_room_id")
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "group_id")
	private Groups group;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "game_type_id")
	private GameType gameType;

	@OneToMany(mappedBy = "gameRoom", orphanRemoval = true, cascade = CascadeType.ALL)
	@Builder.Default
	private List<GameSession> gameSessions = new ArrayList<>();

	private String name;

	private LocalDateTime startedAt;

	private LocalDateTime endedAt;

	@Enumerated(EnumType.STRING)
	private GameRoomStatus gameRoomStatus;

	@Builder.Default
	@ColumnDefault("1")
	private Integer maxPlayerCount = 1;
}
