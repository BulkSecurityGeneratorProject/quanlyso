package logia.quanlyso.service.impl;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import logia.quanlyso.domain.Channel;
import logia.quanlyso.domain.Code;
import logia.quanlyso.domain.CostFactor;
import logia.quanlyso.domain.Factor;
import logia.quanlyso.domain.ProfitFactor;
import logia.quanlyso.domain.Style;
import logia.quanlyso.domain.StyleConstants;
import logia.quanlyso.domain.TransactionDetails;
import logia.quanlyso.domain.Transactions;
import logia.quanlyso.domain.Types;
import logia.quanlyso.domain.TypesConstants;
import logia.quanlyso.repository.CodeRepository;
import logia.quanlyso.repository.CostFactorRepository;
import logia.quanlyso.repository.ProfitFactorRepository;
import logia.quanlyso.repository.TransactionDetailsRepository;
import logia.quanlyso.repository.TransactionsRepository;
import logia.quanlyso.service.CodeService;
import logia.quanlyso.service.dto.CodeDTO;
import logia.quanlyso.service.mapper.CodeMapper;

/**
 * Service Implementation for managing Code.
 *
 * @author Dai Mai
 */
@Service
@Transactional
public class CodeServiceImpl implements CodeService {

	/** The log. */
	private final Logger						log	= LoggerFactory
			.getLogger(CodeServiceImpl.class);

	/** The code repository. */
	private final CodeRepository				codeRepository;

	/** The code mapper. */
	private final CodeMapper					codeMapper;

	/** The cost factor repository. */
	private final CostFactorRepository			costFactorRepository;

	/** The profit factor repository. */
	private final ProfitFactorRepository		profitFactorRepository;

	private final TransactionsRepository		transactionsRepository;

	private final TransactionDetailsRepository	transactionDetailsRepository;

	/**
	 * Instantiates a new code service impl.
	 *
	 * @param codeRepository the code repository
	 * @param codeMapper the code mapper
	 * @param costFactorRepository the cost factor repository
	 * @param profitFactorRepository the profit factor repository
	 * @param transactionsRepository the transactions repository
	 * @param transactionDetailsRepository the transaction details repository
	 */
	public CodeServiceImpl(CodeRepository codeRepository, CodeMapper codeMapper,
			CostFactorRepository costFactorRepository,
			ProfitFactorRepository profitFactorRepository,
			TransactionsRepository transactionsRepository,
			TransactionDetailsRepository transactionDetailsRepository) {
		this.codeRepository = codeRepository;
		this.codeMapper = codeMapper;
		this.costFactorRepository = costFactorRepository;
		this.profitFactorRepository = profitFactorRepository;
		this.transactionsRepository = transactionsRepository;
		this.transactionDetailsRepository = transactionDetailsRepository;
	}

	/**
	 * Save a code.
	 *
	 * @param codeDTO the entity to save
	 * @return the persisted entity
	 */
	@Override
	public CodeDTO save(CodeDTO codeDTO) {
		this.log.debug("Request to save Code : {}", codeDTO);
		Code code = this.codeMapper.toEntity(codeDTO);
		code = this.codeRepository.save(code);
		CodeDTO result = this.codeMapper.toDto(code);
		return result;
	}

	/**
	 * Get all the codes.
	 * 
	 * @param pageable the pagination information
	 * @return the list of entities
	 */
	@Override
	@Transactional(readOnly = true)
	public Page<CodeDTO> findAll(Pageable pageable) {
		this.log.debug("Request to get all Codes");
		Page<Code> result = this.codeRepository.findAll(pageable);
		return result.map(code -> this.codeMapper.toDto(code));
	}

	/**
	 * Get one code by id.
	 *
	 * @param id the id of the entity
	 * @return the entity
	 */
	@Override
	@Transactional(readOnly = true)
	public CodeDTO findOne(Long id) {
		this.log.debug("Request to get Code : {}", id);
		Code code = this.codeRepository.findOne(id);
		CodeDTO codeDTO = this.codeMapper.toDto(code);
		return codeDTO;
	}

	/**
	 * Delete the code by id.
	 *
	 * @param id the id of the entity
	 */
	@Override
	public void delete(Long id) {
		this.log.debug("Request to delete Code : {}", id);
		this.codeRepository.delete(id);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see logia.quanlyso.service.CodeService#calculate(logia.quanlyso.domain.Transactions)
	 */
	@Override
	public Transactions calculate(Transactions transactions) {
		String chosenNumber = transactions.getChosenNumber();
		float netValue = transactions.getNetValue();
		for (TransactionDetails details : transactions.getTransactionDetails()) {
			// Get all condition
			Channel channel = details.getChannels();
			Factor factor = details.getFactors();
			Style style = details.getStyles();
			Types types = details.getTypes();

			// Assume transactions of current date
			ZonedDateTime today = ZonedDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh"));
			ZonedDateTime formattedDate = today.withHour(0).withMinute(0).withSecond(0).withNano(0);
			List<Code> listCodes = this.codeRepository.findAllByChannelsAndOpenDate(channel,
					formattedDate);
			boolean isMatchCondition = this.checkCondition(chosenNumber, listCodes, style, types);
			if (isMatchCondition) {
				CostFactor costFactor = this.costFactorRepository
						.findOneByFactorsAndStylesAndTypes(factor, style, types);
				ProfitFactor profitFactor = this.profitFactorRepository
						.findOneByFactorsAndStylesAndTypes(factor, style, types);
				float amount = details.getAmount();
				details.costs(amount * costFactor.getRate())
				.profit(amount * profitFactor.getRate());

				netValue = netValue + details.getProfit() - details.getCosts();
			}
		}
		transactions.setNetValue(netValue);

		return transactions;
	}

	/**
	 * Check condition.
	 *
	 * @param chosenNumber the chosen number
	 * @param listCodes the list codes
	 * @param style the style
	 * @param types the types
	 * @return true, if successful
	 */
	private boolean checkCondition(String chosenNumber, List<Code> listCodes, Style style,
			Types types) {
		List<Code> filtered = new ArrayList<>();

		if (types.getId().equals(TypesConstants.TOP.getId())) {
			if (style.getId().equals(StyleConstants.TWO_NUM.getId())) {
				filtered = this.filterCodeOnTop(listCodes, 2);
			}
			else if (style.getId().equals(StyleConstants.THREE_NUM.getId())) {
				filtered = this.filterCodeOnTop(listCodes, 3);
			}
		}
		else if (types.getId().equals(TypesConstants.BOTH.getId())) {
			if (style.getId().equals(StyleConstants.TWO_NUM.getId())) {
				filtered = this.filterCodeOnTop(listCodes, 2);
			}
			else if (style.getId().equals(StyleConstants.THREE_NUM.getId())) {
				filtered = this.filterCodeOnTop(listCodes, 3);
			}
			filtered.addAll(this.filterCodeOnBottom(listCodes));
		}
		else if (types.getId().equals(TypesConstants.BOTTOM.getId())) {
			filtered = this.filterCodeOnBottom(listCodes);
		}
		else if (types.getId().equals(TypesConstants.ROLL.getId())) {
			filtered = listCodes;
		}

		for (Code code : filtered) {
			if (code.getCode().contains(chosenNumber)) {
				return true;
			}
		}
		return false;
	}

	private List<Code> filterCodeOnTop(List<Code> codes, int length) {
		List<Code> filtered = new ArrayList<>();
		filtered = codes.stream().filter(code -> code.getCode().length() == length)
				.collect(Collectors.toList());
		return filtered;
	}

	private List<Code> filterCodeOnBottom(List<Code> codes) {
		List<Code> filtered = new ArrayList<>();
		filtered = codes.stream().filter(code -> code.getCode().length() == 6)
				.collect(Collectors.toList()); // 6 is max length of code
		return filtered;
	}
}
