CREATE TABLE operations (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL,
    type VARCHAR(64) NOT NULL,
    status VARCHAR(32) NOT NULL,
    resource_type VARCHAR(64),
    resource_id UUID,
    error_code VARCHAR(80),
    error_message VARCHAR(500),
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
    completed_at TIMESTAMP WITH TIME ZONE,
    CONSTRAINT fk_operations_user FOREIGN KEY (user_id) REFERENCES users (id)
);

CREATE INDEX ix_operations_user_status ON operations (user_id, status);
CREATE INDEX ix_operations_created_at ON operations (created_at);

CREATE TABLE resumes (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL,
    file_name VARCHAR(255) NOT NULL,
    storage_key VARCHAR(500) NOT NULL,
    file_type VARCHAR(120) NOT NULL,
    file_size_bytes BIGINT NOT NULL,
    parsed_status VARCHAR(32) NOT NULL,
    active_flag BOOLEAN NOT NULL,
    uploaded_at TIMESTAMP WITH TIME ZONE NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
    CONSTRAINT fk_resumes_user FOREIGN KEY (user_id) REFERENCES users (id)
);

CREATE INDEX ix_resumes_user_uploaded_at ON resumes (user_id, uploaded_at);
CREATE INDEX ix_resumes_user_active ON resumes (user_id, active_flag);
