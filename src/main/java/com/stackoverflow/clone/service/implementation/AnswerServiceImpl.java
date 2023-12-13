package com.stackoverflow.clone.service.implementation;

import com.stackoverflow.clone.entity.Answer;
import com.stackoverflow.clone.entity.Question;
import com.stackoverflow.clone.entity.User;
import com.stackoverflow.clone.entity.Vote;
import com.stackoverflow.clone.repository.AnswerRepository;
import com.stackoverflow.clone.repository.VoteRepository;
import com.stackoverflow.clone.service.AnswerService;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AnswerServiceImpl implements AnswerService {
    private final AnswerRepository answerRepository;
    private VoteRepository voteRepository;

    public AnswerServiceImpl(AnswerRepository answerRepository, VoteRepository voteRepository){
        this.answerRepository = answerRepository;
        this.voteRepository = voteRepository;
    }

    @Override
    public void save(Answer answer) {
        answerRepository.save(answer);
    }

    @Override
    public List<Answer> findByQuestionId(Long questionId) {
        return answerRepository.findByQuestionId(questionId);
    }

    @Override
    public Answer findById(Long id) {
        return answerRepository.findById(id).get();
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        if(answerRepository.existsById(id)){
            answerRepository.deleteById(id);
        }
    }

    @Override
    public List<Answer> findFirst5ByUserOrderByCreatedAtDesc(User user) {
        return answerRepository.findFirst5ByUserOrderByCreatedAtDesc(user);
    }

    @Override
    public Page<Answer> findAllByUserOrderByCreatedAtDesc(User user, Pageable pageable) {
        return answerRepository.findAllByUserOrderByCreatedAtDesc(user,pageable);
    }

    public Vote findVoteByUserAndAnswer(User user, Answer answer) {
        return voteRepository.findVoteByUserAndAnswer(user,answer);
    }

    @Override
    public void updateVote(Vote existingVote) {
        voteRepository.save(existingVote);
    }

    @Override
    public void createVote(Vote vote) {
        voteRepository.save(vote);
    }

    @Override
    public List<Answer> findUnverifiedAnswersByUser(User user) {
        return answerRepository.findUnverifiedAnswersByUser(user);
    }
}
