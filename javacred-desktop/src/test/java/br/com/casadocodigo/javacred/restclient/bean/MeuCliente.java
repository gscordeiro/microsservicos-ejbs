package br.com.casadocodigo.javacred.restclient.bean;


public class MeuCliente {
    private Integer id;
    private String nome;
    private boolean preferencial;

    public MeuCliente() {
    }

    public MeuCliente(String nome) {
        this.nome = nome;
    }

    @Override
    public String toString() {
        return "MeuCliente [id=" + id + ", nome=" + nome + ", preferencial=" + preferencial + "]";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MeuCliente)) return false;

        MeuCliente cliente = (MeuCliente) o;

        return id.equals(cliente.id);
    }

	@Override
	public int hashCode() {
		int result = id != null ? id.hashCode() : 0;
		result = 31 * result + (nome != null ? nome.hashCode() : 0);
		result = 31 * result + (preferencial ? 1 : 0);
		return result;
	}

	public Integer getId() {
			return id;
		}

		public void setId(Integer id) {
			this.id = id;
		}

		public String getNome() {
			return nome;
		}

		public void setNome(String nome) {
			this.nome = nome;
		}

		public boolean isPreferencial() {
			return preferencial;
		}

		public void setPreferencial(boolean preferencial) {
			this.preferencial = preferencial;
		}

	}
