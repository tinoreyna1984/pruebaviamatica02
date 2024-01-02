CREATE OR REPLACE FUNCTION totalActiveUsers()
RETURNS INTEGER AS $$
DECLARE
    contador INTEGER;
BEGIN
    EXECUTE 'SELECT count(*) FROM usuarios u WHERE u.account_non_expired = true' INTO contador;
    RETURN contador;
END;
$$ LANGUAGE plpgsql;
