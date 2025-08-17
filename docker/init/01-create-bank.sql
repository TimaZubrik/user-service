DO
$do$
BEGIN
   IF NOT EXISTS (SELECT 1 FROM pg_database WHERE datname = 'bank') THEN
      EXECUTE 'CREATE DATABASE bank OWNER postgres';
   END IF;
END
$do$;