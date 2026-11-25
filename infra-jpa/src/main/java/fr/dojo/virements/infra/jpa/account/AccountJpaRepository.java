package fr.dojo.virements.infra.jpa.account;

import org.springframework.data.jpa.repository.JpaRepository;

interface AccountJpaRepository extends JpaRepository<AccountEntity, String> {
}
