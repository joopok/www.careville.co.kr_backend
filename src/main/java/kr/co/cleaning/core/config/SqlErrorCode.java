package kr.co.cleaning.core.config;

import java.util.HashMap;
import java.util.Map;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.jdbc.CannotGetJdbcConnectionException;

public enum SqlErrorCode {

	BAD_SQL_GRAMMAR("42000"				, BadSqlGrammarException.class				,"SQL 문법 오류"),
    DUPLICATE_KEY("23000"				, DuplicateKeyException.class				,"유니크/중복 키 위반"),
    DATA_INTEGRITY_VIOLATION("23000"	, DataIntegrityViolationException.class		,"무결성 제약 위반"),
    CANNOT_GET_JDBC_CONNECTION("08001"	, CannotGetJdbcConnectionException.class	,"DB 연결 실패"),
    UNKNOWN(null						, DataAccessException.class					,"알 수 없는 SQL 오류");

    private final String sqlState;
    private final Class<? extends DataAccessException> exceptionClass;
    private final String message;

    private static final Map<String, SqlErrorCode> STATE_MAP = new HashMap<>();
    private static final Map<Class<?>, SqlErrorCode> CLASS_MAP = new HashMap<>();

    static {
        for (SqlErrorCode code : values()) {
            if (code.sqlState != null) {
                STATE_MAP.put(code.sqlState, code);
            }
            CLASS_MAP.put(code.exceptionClass, code);
        }
    }

    SqlErrorCode(String sqlState, Class<? extends DataAccessException> exceptionClass, String message) {
        this.sqlState = sqlState;
        this.exceptionClass = exceptionClass;
        this.message = message;
    }

    public String getSqlState() {
        return sqlState;
    }

    public Class<? extends DataAccessException> getExceptionClass() {
        return exceptionClass;
    }

    public String getMessage() {
        return message;
    }

    /** SQLState로 찾기 */
    public static SqlErrorCode fromSqlState(String sqlState) {
        return STATE_MAP.getOrDefault(sqlState, UNKNOWN);
    }

    /** 예외 클래스 타입으로 찾기 */
    public static SqlErrorCode fromException(DataAccessException ex) {
        return CLASS_MAP.getOrDefault(ex.getClass(), UNKNOWN);
    }
}
