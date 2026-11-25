package fr.dojo.virements.domaine.common.exception;

import java.io.Serializable;

public record BusinessError(String code, String label) implements Serializable {
}
