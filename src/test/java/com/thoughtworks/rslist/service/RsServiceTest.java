package com.thoughtworks.rslist.service;

import com.thoughtworks.rslist.domain.Trade;
import com.thoughtworks.rslist.domain.Vote;
import com.thoughtworks.rslist.dto.RsEventDto;
import com.thoughtworks.rslist.dto.TradeDto;
import com.thoughtworks.rslist.dto.UserDto;
import com.thoughtworks.rslist.dto.VoteDto;
import com.thoughtworks.rslist.repository.RsEventRepository;
import com.thoughtworks.rslist.repository.TradeRepository;
import com.thoughtworks.rslist.repository.UserRepository;
import com.thoughtworks.rslist.repository.VoteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

class RsServiceTest {
  RsService rsService;

  @Mock
  RsEventRepository rsEventRepository;
  @Mock
  UserRepository userRepository;
  @Mock
  VoteRepository voteRepository;
  @Mock
  TradeRepository tradeRepository;
  LocalDateTime localDateTime;
  Vote vote;
  Trade trade;

  @BeforeEach
  void setUp() {
    initMocks(this);
    rsService = new RsService(rsEventRepository, userRepository, voteRepository, tradeRepository);
    localDateTime = LocalDateTime.now();
   // vote = Vote.builder().voteNum(2).rsEventId(1).time(localDateTime).userId(1).build();
    trade = Trade.builder().rsEventId(1).userId(1).amount(100).rank(1).build();
  }

  @Test
  void shouldVoteSuccess() {
    // given
    vote = Vote.builder().voteNum(2).rsEventId(1).time(localDateTime).userId(1).build();

    UserDto userDto =
        UserDto.builder()
            .voteNum(5)
            .phone("18888888888")
            .gender("female")
            .email("a@b.com")
            .age(19)
            .userName("xiaoli")
            .id(2)
            .build();
    RsEventDto rsEventDto =
        RsEventDto.builder()
            .eventName("event name")
            .id(1)
            .keyword("keyword")
            .voteNum(2)
            .user(userDto)
            .build();
    when(rsEventRepository.findById(anyInt())).thenReturn(Optional.of(rsEventDto));
    when(userRepository.findById(anyInt())).thenReturn(Optional.of(userDto));
    // when
    rsService.vote(vote, 1);
    // then
    verify(voteRepository)
        .save(
            VoteDto.builder()
                .num(2)
                .localDateTime(localDateTime)
                .user(userDto)
                .rsEvent(rsEventDto)
                .build());
    verify(userRepository).save(userDto);
    verify(rsEventRepository).save(rsEventDto);
  }

  @Test
  void shouldThrowExceptionWhenUserNotExist() {
    // given
    vote = Vote.builder().voteNum(3).rsEventId(1).time(localDateTime).userId(1).build();
    when(rsEventRepository.findById(anyInt())).thenReturn(Optional.empty());
    when(userRepository.findById(anyInt())).thenReturn(Optional.empty());
    //when&then
    assertThrows(
        RuntimeException.class,
        () -> {
          rsService.vote(vote, 1);
        });
  }

  @Test
  void shouldThrowExceptionWhenVoteNotValid() {
    // given
    vote = Vote.builder().voteNum(3).rsEventId(1).time(localDateTime).userId(1).build();
    UserDto userDto =
            UserDto.builder()
                    .voteNum(2)
                    .phone("18888888888")
                    .gender("female")
                    .email("a@b.com")
                    .age(19)
                    .userName("xiaoli")
                    .id(2)
                    .build();
    RsEventDto rsEventDto =
            RsEventDto.builder()
                    .eventName("event name")
                    .id(1)
                    .keyword("keyword")
                    .voteNum(3)
                    .user(userDto)
                    .build();
    when(rsEventRepository.findById(anyInt())).thenReturn(Optional.of(rsEventDto));
    when(userRepository.findById(anyInt())).thenReturn(Optional.of(userDto));

    // when & then
    assertThrows(RuntimeException.class, () -> {
        rsService.vote(vote,1);
    });
  }

  @Test
  void shouldTradeSuccess() {
    // given
    UserDto userDto =
            UserDto.builder()
                    .voteNum(5)
                    .phone("18888888888")
                    .gender("female")
                    .email("a@b.com")
                    .age(19)
                    .userName("xiaoli")
                    .id(2)
                    .build();
    RsEventDto rsEventDto =
            RsEventDto.builder()
                    .eventName("event name")
                    .id(1)
                    .keyword("keyword")
                    .voteNum(2)
                    .user(userDto)
                    .build();

    VoteDto voteDto = VoteDto.builder().rsEvent(rsEventDto)
            .user(userDto).num(3).localDateTime(localDateTime).build();
    when(rsEventRepository.findById(anyInt())).thenReturn(Optional.of(rsEventDto));
    when(userRepository.findById(anyInt())).thenReturn(Optional.of(userDto));
  //  when(voteRepository.findById(anyInt())).thenReturn(Optional.of(voteDto));
    // when
    rsService.buy(trade, 1);
    // then
    verify(tradeRepository)
            .save(
                    TradeDto.builder()
                            .amount(100)
                            .rank(1)
                            .rsEvent(rsEventDto)
                            .user(userDto)
                            .build());
    verify(userRepository).save(userDto);
    verify(rsEventRepository).save(rsEventDto);
   // verify(voteRepository).save(voteDto);
  }
}
